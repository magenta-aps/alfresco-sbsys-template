#!/bin/bash

curl -i -k -H "@headers.txt" -X POST -F 'json={"SagId":979,"Navn":"curlTest"}' -F 'files=@/home/andreas/Dropbox/testDocs/test1.docx' "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde"
