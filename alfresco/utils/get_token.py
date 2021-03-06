import json


def get_token():
    with open('json/token.json') as f:
        token = json.load(f)['access_token']
    return token


with open('headers.txt', 'w') as f:
    f.write('Authorization: Bearer ' + get_token())

# Insert token into JSON files


def add_token(filename):
    with open('json/' + filename) as f:
        payload = json.load(f)
        payload['token'] = {'token': get_token()}
    
    with open('json/' + filename.split('.')[0] + '_temp.json', 'w') as f:
        json.dump(payload, f)


add_token('post_merge.json')
add_token('post_upload.json')
add_token('post_preview.json')

with open('json/post_multipart.json') as f:
    payload = json.load(f)
    payload['token'] = get_token()

with open('json/post_multipart_temp.json', 'w') as f:
    json.dump(payload, f)


