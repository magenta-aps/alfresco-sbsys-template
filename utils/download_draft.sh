#!/bin/bash

curl --http1.1 -s -k -H "@headers.txt" "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde/$1/readonly/1" > /tmp/download.docx
