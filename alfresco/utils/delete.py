import sys
import json
import requests
from credentials import admin_basic_auth

node_id = sys.argv[1]

alf_delete_draft = 'http://localhost:8080/alfresco/s/template/draft/delete/' + node_id

r = requests.delete(alf_delete_draft, auth=admin_basic_auth)

print('DELETE ' + alf_delete_draft)
print(r.status_code)
print()
