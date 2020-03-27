# alfresco-sbsys-template

Project for handeling document templates to be used with SBSYS. A number of document templates 
are stored in Alfresco and SBSYS can (via REST) access a list of these templates. The SBSYS user 
can select a template and send this back to Alfresco along with an SBSYS case id. Alfresco then 
retreives case metadata from SBSYS and merges these data into the template. Finally, the 
filled out template is send back to SBSYS.

_NOTE: This is a proof-of-concept project, i.e. no test are written and proper error 
handling (with respect to the HTTP communication between Alfresco and SBSYS) is missing 
in the code base_.

This project is (for historic) reasons made for Java 8 which means that HTTP/2 communication 
cannot be handled. This is, however, required when communicating with SBSYS, so this project 
makes use of the utils project found in here: [multipart-form-data-requester](https://github.com/magenta-aps/multipart-form-data-requester).

# Development

## Hot reloading

This project was build with
```
$ mvn archetype:generate -Dfilter=org.alfresco:
```
The SDK version was chosen to 3.1 and the platform-jar project (number 4) was chosen.
The AMP section in the POM was enabled (this was all that was needed in order to build 
an AMP file in the `target`).

Follow the instructions on [https://docs.alfresco.com/5.2/tasks/sdk-hot-reload-hotswap.html](https://docs.alfresco.com/5.2/tasks/sdk-hot-reload-hotswap.html) carefully

1. Download the latest DCEVM release (for Java 1.8_181) from [here](https://github.com/dcevm/dcevm/releases)
2. Download the corresponding version of Java from [here](https://adoptopenjdk.net/archive.html) 
   and place this in `/opt`
3. In every command that is run, make sure to set `JAVA_HOME=/opt/jdk8u181-b13`.
4. Regarding point 3 in Alfrescos guide... add the folder `/opt/jdk8u181-b13` and select this before 
   clicking "Install DCEVM as altjvm"
5. When downloading the HotswapAgent, make sure to use [version 1.0](https://github.com/HotswapProjects/HotswapAgent/releases/tag/1.0)
6. Remember to hit "Recompile" in IntelliJ when making a change in a Java file

The command in `run.sh` should look like this:
```
JAVA_HOME="/opt/jdk8u181-b13" MAVEN_OPTS="-Xms256m -Xmx2G 
-javaagent:/home/andreas/downloads/hotswap-agent.jar 
-XXaltjvm=dcevm" mvn clean install alfresco:run
```

This seems to work!

## Remote debugging

Use the `debug.sh` file instead of the `run.sh` (or modify this file 
accordingly). Make a new "Run configuration" in IntelliJ (choose the type 
"Remote") from the list. Change the port from 5005 to 8000. Fire up the 
system using the `debug.sh/run.sh` and then start the debugger in IntelliJ. 
Note that the development environment will be hanging until the remote debugger 
is started from the IDE.

Also see this: [https://docs.alfresco.com/5.2/tasks/sdk-debug-intellij.html](https://docs.alfresco.com/5.2/tasks/sdk-debug-intellij.html)

## Set debug log levels

In `tomcat/shared/classes/alfresco/extension/custom-log4j.properties`:
```
# Set root logger level to error
log4j.rootLogger=error, Console, File

###### Console appender definition #######

# All outputs currently set to be a ConsoleAppender.
log4j.appender.Console=org.apache.log4j.ConsoleAppender
log4j.appender.Console.layout=org.apache.log4j.PatternLayout

# use log4j NDC to replace %x with tenant domain / username
log4j.appender.Console.layout.ConversionPattern=%d{ISO8601} %x %-5p [%c{3}] [%t] %m%n 
#log4j.appender.Console.layout.ConversionPattern=%d{ABSOLUTE} %-5p [%c] %m%n

###### File appender definition #######
log4j.appender.File=org.apache.log4j.DailyRollingFileAppender
log4j.appender.File.File=alfresco.log
log4j.appender.File.Append=false
log4j.appender.File.DatePattern='.'yyyy-MM-dd
log4j.appender.File.layout=org.apache.log4j.PatternLayout
log4j.appender.File.layout.ConversionPattern=%d{yyyy-MM-dd} %d{ABSOLUTE} %-5p [%c] [%t] %m%n

###### Hibernate specific appender definition #######
#log4j.appender.file=org.apache.log4j.FileAppender
#log4j.appender.file.File=hibernate.log
#log4j.appender.file.layout=org.apache.log4j.PatternLayout
#log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### Magenta ###
log4j.logger.dk.magenta.alfresco.sbsys.template=DEBUG
log4j.logger.com.parashift.onlyoffice.CallBack=DEBUG
#log4j.logger.org.alfresco.repo.transaction=DEBUG
```

## Things to note about deployment

These things need to be done (at least in the POC) within the Tomcat eexploded WAR 
folder for Alfresco, i.e. `tomcat/webapps/alfresco`:

### CORS filter settings

Make the appropriate CORS filter settings as described here: 
[https://docs.alfresco.com/5.2/tasks/enable-cors.html](https://docs.alfresco.com/5.2/tasks/enable-cors.html)

### Compile and copy in the OnlyOffice JAR
Note that this repo should be moved to the Magenta GitHub organization...
1. `git clone https://github.com/andreaskring/onlyoffice-alfresco.git`
1. `git checkout origin/forcesave`
1. `mvn clean package`
1. Copy the JAR file from the `target` folder to `tomcat/webapps/alfresco/WEB-INF/lib`

 