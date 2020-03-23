import json
import sys
import requests
from credentials import admin_basic_auth

from get_token import add_token
from get_token import get_token

kladde = sys.argv[1]

ALF_PREVIEW_DRAFT = 'http://localhost:8080/alfresco/s/template/draft/edit'
ALF_UPLOAD_URL = 'http://localhost:8080/alfresco/s/template/upload'

add_token('post_edit.json')
with open('json/post_edit_temp.json') as f:
    payload = json.load(f)
checkout_template = payload.get('urls').get('filcheckud')
checkin_template = payload.get('urls').get('filcheckind')
payload['urls']['filcheckud'] = checkout_template % kladde
payload['urls']['filcheckind'] = checkin_template % kladde

r = requests.post(
    ALF_PREVIEW_DRAFT,
    json=payload,
    auth=admin_basic_auth
)

pre_upload = r.json()

# Check-out the draft to Alfresco

print('Start editing... (checkout)')
print('POST ' + ALF_PREVIEW_DRAFT)
print(r.status_code)
print(json.dumps(pre_upload, indent=2))
print()

# Check-in the final draft from Alfresco to SBSYS

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

    print('Upload document (check-in)')
    print('POST ' + ALF_UPLOAD_URL)
    print(r.status_code)

    if r.status_code == 200:
        success = True
