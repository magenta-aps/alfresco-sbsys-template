import json
import requests
from credentials import admin_basic_auth

from get_token import add_token
from get_token import get_token

ALF_PREVIEW_DRAFT = 'http://localhost:8080/alfresco/s/template/draft/preview'

add_token('post_preview.json')
with open('json/post_preview_temp.json') as f:
    payload = json.load(f)

r = requests.post(
    ALF_PREVIEW_DRAFT,
    json=payload,
    auth=admin_basic_auth
)

pre_upload = r.json()

print('POST ' + ALF_PREVIEW_DRAFT)
print(r.status_code)
print(json.dumps(pre_upload, indent=2))
print()
