#!/bin/bash

#JAVA_HOME="/opt/jdk8u181-b13" MAVEN_OPTS="-Xms256m -Xmx2G -javaagent:/home/andreas/downloads/hotswap-agent.jar -XXaltjvm=dcevm" mvn install alfresco:run
#JAVA_HOME="/opt/jdk8u181-b13" MAVEN_OPTS="-Xms256m -Xmx8G -javaagent:/home/andreas/downloads/hotswap-agent.jar -XXaltjvm=dcevm" mvn clean install alfresco:run
JAVA_HOME="/opt/jdk8u181-b13" MAVEN_OPTS="-Xms256m -Xmx2G -javaagent:/home/andreas/downloads/hotswap-agent.jar -XXaltjvm=dcevm" mvnDebug clean install alfresco:run
#JAVA_HOME="/opt/jdk8u181-b13" MAVEN_OPTS="-Xms256m -Xmx8G" mvn clean install alfresco:run
