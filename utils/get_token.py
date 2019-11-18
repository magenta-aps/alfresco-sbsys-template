import json

with open('json/token.json') as f:
    token = json.load(f)['access_token']

with open('headers.txt', 'w') as f:
    f.write('Authorization: Bearer ' + token)


