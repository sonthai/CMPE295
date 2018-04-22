from os import listdir
from os.path import isfile, join
import base64
import time
import urllib2
import json
import os
from PIL import Image
import threading

path = 'images'
url = 'http://35.165.214.31:8080/app-1/customer/processData'

def execute():
    threading.Time(5.0, execute).start()
    listOfFiles = [f for f in listdir(path) if isfile(join(path, f))]
    for file in listOfFiles:
       path_file = path+'/'+file
       im = Image.open(path_file)
       width, height = im.size
       # print width, height
       # im.show()
       # cropping_90_size = ( width * .1, height * .1,  width - width * .1, height - height * .1)
       cropping_no_top_size = ( 0, height * .2,  width, height)
       cropping_size = cropping_no_top_size
       # cropping_size = cropping_90_size
       # print cropping_size
       cropped_im = im.crop(cropping_size)
       # cropped_im.show()
       cropped_im.save(path_file)
       with open(path_file, "rb") as image_file:
           encoded_string = base64.b64encode(image_file.read())
           ts = int(time.time())
           payload = {}
           payload['id'] = ts
           payload['image'] = encoded_string
           headers = {'Content-type': 'application/json'}
           data_json = json.dumps(payload)
           request = urllib2.Request(url)
           request.add_header('Content-Type', 'application/json')
           response = urllib2.urlopen(request, data_json)
           print path_file+" was sent!"
           os.remove(path_file)
execute()
