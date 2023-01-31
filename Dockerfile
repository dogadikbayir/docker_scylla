# get Linux
FROM alpine:latest
WORKDIR /app/
COPY . /app

# Set up curl and Java
RUN apk update \
&& apk upgrade \
&& apk add --no-cache bash \
&& apk add --no-cache --virtual=build-dependencies unzip \
&& apk add --no-cache curl \
&& apk add --no-cache openjdk13-jre


# GET Python
RUN apk add --no-cache python3 \
&& python3 -m ensurepip \
&& pip3 install --upgrade pip setuptools \
&& rm -r /usr/lib/python*/ensurepip && \
if [ ! -e /usr/bin/pip ]; then ln -s pip3 /usr/bin/pip ; fi && \
if [[ ! -e /usr/bin/python ]]; then ln -sf /usr/bin/python3 /usr/bin/python; fi && \
rm -r /root/.cache

# Switch directory to api (to start the Flask listener)
WORKDIR /app/api

# Get requirements for python
RUN pip install -r requirements.txt

# show port 8000 to the outside world
EXPOSE 8000 

# run the Flask listener on port 8000
CMD ["python3", "apiTool.py"]
