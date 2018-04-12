from os import listdir
from os.path import isfile, join
import base64
import time
import urllib2
import json
import os

path = ‘images’
url = ‘http://35.165.214.31:8080/app-1/customer/processData’
listOfFiles = [f for f in listdir(path) if isfile(join(path, f))]
print listOfFiles

for file in listOfFiles:
   path_file = path+“/”+file
   with open(path_file, “rb”) as image_file:
       encoded_string = base64.b64encode(image_file.read())
       ts = int(time.time())
       payload = {}
       payload[‘id’] = ts
       payload[‘image’] = encoded_string
       headers = {‘Content-type’: ‘application/json’}
       data_json = json.dumps(payload)
       request = urllib2.Request(url)
       request.add_header(‘Content-Type’, ‘application/json’)
       response = urllib2.urlopen(request, data_json)
       print response
       os.remove(path_file)
