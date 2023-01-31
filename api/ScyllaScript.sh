#! /usr/bin/bash

cd ../scylla
java -cp "./target/classes:../dependencies/*:lib/*:*" de.hpi.bpt.scylla.Scylla --config=samples/Kreditkarte_global_1.xml --bpmn=samples/Kreditkarte_1.bpmn --sim=samples/Kreditkarte_sim_1.xml --enable-bps-logging
