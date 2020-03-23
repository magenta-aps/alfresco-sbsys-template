# Multipart/form-data Requester

This project is a utility project to be used together with the 
[alfresco-sbsys-template](https://github.com/magenta-aps/alfresco-sbsys-template) project. 
It receives data from Alfresco and passes these on to an SBSYS (SBSIP) server. It is only 
a proof-of-concept project. The reason that a standalone project is needed is that the 
alfresco-sbsys-template project uses Java 8 and there are problem making HTTP/2 requests 
in Java 8. This project fixes the issue by making use of Java 11 libs. I.e. it 
basically works as a "proxy" for Alfresco, when Alfresco is making HTTP/2 requests to SBSYS.