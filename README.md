# Hot reloading

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

# Remote debugging

Use the `debug.sh` file instead of the `run.sh` (or modify this file 
accordingly). Make a new "Run configuration" in IntelliJ (choose the type 
"Remote") from the list. Change the port from 5005 to 8000. Fire up the 
system using the `debug.sh/run.sh` and then start the debugger in IntelliJ. 
Note that the development environment will be hanging until the remote debugger 
is started from the IDE.

Also see this: [https://docs.alfresco.com/5.2/tasks/sdk-debug-intellij.html](https://docs.alfresco.com/5.2/tasks/sdk-debug-intellij.html)
