import json

with open('json/token.json') as f:
    token = json.load(f)['access_token']

with open('headers.txt', 'w') as f:
    f.write('Authorization: Bearer ' + token)

#print(token)

# Insert token into post_template.json

with open('json/post_template.json') as f:
    payload = json.load(f)
    payload['token'] = {'token': token}

with open('json/post_template_temp.json', 'w') as f:
    json.dump(payload, f) 

