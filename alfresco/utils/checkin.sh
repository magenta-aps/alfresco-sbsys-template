#!/bin/bash

curl -i -k -H "@headers.txt" -X POST -F 'json={}' -F files=@/home/andreas/Dropbox/testDocs/$2 "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde/$1/checkin"
