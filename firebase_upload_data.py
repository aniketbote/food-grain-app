import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from google.cloud import storage
import os
import pandas as pd
from pprint import pprint

final_dict = {}
cat = ['Exotic_Vegetables.csv','Exotic_Fruits.csv','Vegetables.csv','Fruits.csv','Foodgrains.csv']

for name in cat:
    path = os.path.join('data',name)
    data = pd.read_csv(path)
    data_cols = list(data.columns)
    name_col = data_cols.pop(0)
    temp_sing = {}
    for i in range(len(data)):
        temp_data = {}
        for col in data_cols:
            if str(type(data[col][i])) == "<class 'str'>":
                temp_data[col] = data[col][i]
                continue
            temp_data[col] = data[col][i].item()
        temp_sing[data[name_col][i]] = temp_data
    final_dict[name.split('.')[0]] = temp_sing

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Desktop/Aniket/food-grain-app/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})

featured = pd.read_csv('data/featured.csv')
featured_list = list(featured['featured'])
final_dict['featured'] = featured_list

# pprint(final_dict)
ref = db.reference('all_items')
ref.set(final_dict)
pprint('Done')
