#!/bin/bash

# Navigate to the directory containing the JAR files
cd /home/ubuntu/holo

# Find the latest JAR file
JAR_FILE=$(ls -Art holobot-*.jar | tail -n 1)

# Start the JAR file
/usr/bin/java -Xmx2G -jar "$JAR_FILE"