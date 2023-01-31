import json

from flask import Flask, request
from flask_restful import Resource, Api, abort
import os

from os import listdir
from os.path import isfile, join
import subprocess
import sys

app = Flask("ToolAPI")
api = Api(app)

# example request:
# curl --location --request PUT 'http://127.0.0.1:8000/storeddata/data3' \
# --header 'Content-Type: application/json' \
# --data-raw '{"key1": "someKey"}'


with open('storedData.json', 'r') as r:
    storedData = json.load(r)


def write_changes_to_file():
    with open('storedData.json', 'w') as w:
        json.dump(storedData, w)


class OurData(Resource):

    def get(self, data_id):
        if data_id == "all":
            print("This is a GET request. Instead of printing we could do sth else")


            return storedData
        if data_id not in storedData:
            print("This is a GET request. Instead of printing we could do sth else")
            abort(404, message=f"Data {data_id} not found!")
        print("This is a GET request. Instead of printing we could do sth else")

        # This command does what ScyllaScript.sh does, just change the input params (read it from the HTTP request) for arbitrary bpmn and config files
        run_scylla_command = 'java -cp /app/scylla/target/classes/:/app/dependencies/*:/app/scylla/lib/*:/app/* de.hpi.bpt.scylla.Scylla --config=/app/scylla/samples/Kreditkarte_global_1.xml --bpmn=/app/scylla/samples/Kreditkarte_1.bpmn --sim=/app/scylla/samples/Kreditkarte_sim_1.xml --enable-bps-logging'
        process = subprocess.Popen(run_scylla_command.split(), stdout=subprocess.PIPE )

        samples_content = os.listdir('/app/scylla/samples/') # when a GET request comes to the Flask listener, we run scylla and print the contents of /app/scylla/samples/ to check whether the experiment folder is created or not.

        # find the entries in samples_content, to find out newly created output folders (they start with 'output_')
        samples_content = [sc for sc in samples_content if sc[:7] == 'output_']


        print('Scylla created the following output folders so far: ')
        print(samples_content, flush=True)

        # print out scylla execution
        print('Scylla execution output: ')
        output, error = process.communicate()
        print(output)
        print(error)

        return storedData[data_id]

    def put(self, data_id):
        data_from_request = request.data.decode('UTF-8')

        print(data_from_request)
        json_object = json.loads(data_from_request)
        new_data = {'key1': json_object['key1']}
        storedData[data_id] = new_data
        write_changes_to_file()
        print("This is a PUT request. Instead of printing we could do sth else")
        return {data_id: storedData[data_id]}, 201

    def delete(self, data_id):
        if data_id not in storedData:
            abort(404, message=f"Data {data_id} not found!")
        del storedData[data_id]
        write_changes_to_file()
        print("This is a DELETE request. Instead of printing we could do sth else")
        return "", 204


class DataOverview(Resource):

    def get(self):
        print("This is a GET request. Instead of printing we could do sth else")
        return storedData

    def post(self):
        data_from_request = request.data.decode('UTF-8')
        json_object = json.loads(data_from_request)
        new_data = {'key1': json_object['key1']}
        data_id = 'data' + str(max(int(v.lstrip('data')) for v in storedData.keys()) + 1)
        storedData[data_id] = new_data
        write_changes_to_file()
        print("This is a POST request. Instead of printing we could do sth else")
        return storedData[data_id], 201


api.add_resource(OurData, '/storeddata/<data_id>')
api.add_resource(DataOverview, '/storeddata')

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 8000))
    app.run(port=port, host='0.0.0.0', debug=True)  # if you don't set host=0.0.0.0, you'll app will be stuck in the Docker container
