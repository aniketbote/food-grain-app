import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from google.cloud import storage
import os
import pandas as pd
from pprint import pprint

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Desktop/Aniket/food-grain-app/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})


def get_reponse(data):
    final_list = []
    for element in data:
        tempDict = {}
        tempDict['name'] = element
        tempDict['description'] = data[element]['Description']
        tempDict['imageUrl'] = data[element]['Image']
        tempDict['size'] = data[element]['Size']
        tempDict['price'] = data[element]['Price']
        tempDict['availableQuantity'] = data[element]['Available Quantity']
        tempDict['type'] = data[element]['Type']
        final_list.append(tempDict)
    return final_list


def popularItems():
    responseDict = {}
    for category in ["Exotic_Vegetables","Exotic_Fruits","Vegetables","Fruits","Foodgrains"]:
        ref = db.reference('all_items/{}'.format(category)).order_by_child("OrderCount").limit_to_first(2)
        refData = ref.get()
        responseDict[category] = get_reponse(refData)

ref = db.reference('all_items/Exotic_Fruits')
snapshot = ref.order_by_child("OrderCount")
refData = snapshot.get()
# pprint(ref.get())

pprint(refData)
