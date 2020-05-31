import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from google.cloud import storage
import os
import pandas as pd
from pprint import pprint

final_dict = {}
final_len_dict = {}
combined_dict = {}
cat = ['Exotic_Vegetables.csv','Exotic_Fruits.csv','Vegetables.csv','Fruits.csv','Foodgrains.csv']

for name in cat:
    path = os.path.join('data',name)
    data = pd.read_csv(path)
    data_cols = list(data.columns)
    name_col = data_cols.pop(0)
    temp_sing = {}
    final_len_dict[name.split('.')[0]] = len(data)
    for i in range(len(data)):
        temp_data_all = {}
        temp_data_combined = {}
        for col in data_cols:
            if str(type(data[col][i])) == "<class 'str'>":
                temp_data_all[col] = data[col][i]
                temp_data_combined[col] = data[col][i]
                continue
            temp_data_all[col] = data[col][i].item()
            temp_data_combined[col] = data[col][i].item()
            temp_data_all['Type'] = name.split('.')[0]
            temp_data_combined['Type'] = name.split('.')[0]
        temp_sing[data[name_col][i]] = temp_data_all
        combined_dict[data[name_col][i]] = temp_data_combined
    final_dict[name.split('.')[0]] = temp_sing

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Desktop/Aniket/food-grain-app/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})

featured = pd.read_csv('data/featured.csv')
featured_list = list(featured['featured'])


# pprint(combined_dict)

# ref = db.reference('combined_items')
# ref.set(combined_dict)

ref = db.reference('all_items')
ref.set(final_dict)

ref = db.reference('featured')
ref.set(featured_list)

ref = db.reference('total_items')
ref.set(final_len_dict)

pprint('Done')
