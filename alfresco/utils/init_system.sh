#!/bin/bash

curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/pre-upload
curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/preview
curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/edit
curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon2.docx
curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon3.docx
curl -i -u admin:admin -X DELETE http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon5.odt

curl -i -u admin:admin -X MKCOL http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/pre-upload
curl -i -u admin:admin -X MKCOL http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/preview
curl -i -u admin:admin -X MKCOL http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/edit
curl -i -u admin:admin -T '../src/main/resources/sandbox/skabelon2.docx' "http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon2.docx"
curl -i -u admin:admin -T '../src/main/resources/sandbox/skabelon3.docx' "http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon3.docx"
curl -i -u admin:admin -T '../src/main/resources/sandbox/skabelon5.odt' "http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon5.odt"
