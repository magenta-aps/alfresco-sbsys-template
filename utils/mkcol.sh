#!/bin/bash

curl -i -u admin:admin -X MKCOL http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/pre-upload
curl -i -u admin:admin -T '../src/main/resources/sandbox/skabelon2.docx' "http://localhost:8080/alfresco/webdav/Sites/swsdp/documentLibrary/skabelon2.docx"
