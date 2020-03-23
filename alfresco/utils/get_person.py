import requests
import json

URL = 'https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/part/person?id=2'

with open('json/token.json') as f:
    token = json.load(f)['access_token']

headers = {'Authorization': 'Bearer ' + token}
r = requests.get(URL, headers=headers, verify=False)

print(json.dumps(r.json(), indent=2))
