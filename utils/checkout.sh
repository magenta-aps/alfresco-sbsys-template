#!/bin/bash

curl -s -k -H "@headers.txt" "https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/kladde/1501/checkout/latest" > /tmp/checkout.docx
