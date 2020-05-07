# Multipart/form-data Requester

This project is a utility project to be used together with the 
[Alfresco application](../alfresco/README.md). 
It receives data from Alfresco and passes these on to the SBSIP server. It is only 
a proof-of-concept project/pilot. The reason that a standalone project is needed is that the 
Alfresco project uses Java 8 and there are problem making HTTP/2 requests 
in Java 8. This project fixes the issue by making use of Java 11 libs. I.e. it 
basically works as a "proxy" for Alfresco, when Alfresco is making HTTP/2 requests to SBSYS.

## Installation

The procedures described here will work for now, but one could argue that things could be 
optimized a bit...

1.  Install Java 11 (the OpenJDK version) on the server.
1.  Build the JAR file locally with
    ```
    $ mvn clean package
    ```
    and copy it to the Alfresco server to the folder `/opt/multipart-form-data-requester`.
1.  Create a file called `/opt/multipart-form-data-requester/start.sh` and 
    make this executable).
1.  Edit the file to contain this content:
    ```
    #!/bin/bash
    
    /usr/lib/jvm/java-11-openjdk-amd64/bin/java -jar /opt/multipart-form-data-requester/multipart-form-data-requester-1.0.0.jar
    ```
1.  Create a `systemd` service file `/etc/systemd/system/multipart-form-data-requester.service` with 
    this content:
    ```
    [Unit]
    Description=Multipart-form-data-requester
    After=network.target
    
    [Service]
    User=alfresco
    Type=simple
    Environment="LANG=da_DK.utf8"
    Environment="JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64"
    ExecStart=/opt/multipart-form-data-requester/start.sh
    StandardOutput=file:/var/log/multipart-form-data-requester.log
    StandardError=file:/var/log/multipart-form-data-requester.log
    
    #ExecStop=/etc/init.d/alfresco stop
    
    [Install]
    WantedBy=multi-user.target
    ```
1.  Enable and startthe service.
