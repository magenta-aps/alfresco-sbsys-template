import json
import requests
from credentials import admin_basic_auth

from get_token import add_token
from get_token import get_token


# SBSYS_GET_CASE_URL = 'https://sbsip-m-01.bk-sbsys.dk:28443/convergens-sbsip-sbsys-webapi-proxy/proxy/api/sag/979'
ALF_GET_TEMPLATE_URL = 'http://localhost:8080/alfresco/s/template'
ALF_MERGE_URL = 'http://localhost:8080/alfresco/s/template/merge'
ALF_UPLOAD_URL = 'http://localhost:8080/alfresco/s/template/upload'

# Get and select a template

r = requests.get(ALF_GET_TEMPLATE_URL, auth=admin_basic_auth)
template = [template for template in r.json() if template.get('filename') == 'skabelon3.docx'][0]

print('GET ' + ALF_GET_TEMPLATE_URL)
print(r.status_code)

# Simulate SBSYS POSTing of the template selection along with case data

add_token('post_merge.json')
with open('json/post_merge_temp.json') as f:
    merge_payload = json.load(f)
merge_payload['id'] = template.get('id')

r = requests.post(
    ALF_MERGE_URL,
    json=merge_payload,
    auth=admin_basic_auth
)

pre_upload = r.json()

print('POST ' + ALF_MERGE_URL)
print(r.status_code)
print(json.dumps(pre_upload, indent=2))
print()

# Simulate SBSYS POSTing of the pre-uploaded document

success = False
while not success:
    input('Press enter to upload...')

    payload = {
        'preUploadId': pre_upload.get('preUploadId'),
        'token': {
            'token': get_token()
        }
    }

    r = requests.post(
        ALF_UPLOAD_URL,
        json=payload,
        auth=admin_basic_auth
    )

    print('POST ' + ALF_UPLOAD_URL)
    print(r.json())
    print(r.status_code)

    if r.status_code == 200:
        success = True
