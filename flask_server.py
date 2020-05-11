from flask import Flask, jsonify, request
import json
from pprint import pprint
import os

#Firebase realtime database
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db


os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Desktop/Aniket/food-grain-app/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})


app = Flask(__name__)

def checkForCurrentOrder(emHash):
    ref = db.reference('all_orders/{}/current'.format(emHash))
    if ref.get() == None:
        return True
    else:
        return False,"No Pending "

def segragateCart(cart_list):
    segragateDict = {}
    Fruits = []
    Exotic_Fruits = []
    Vegetables = []
    Exotic_Vegetables = []
    Foodgrains = []
    for cart_item in cart_list:
        if cart_item['type'] == "Fruits":
            Fruits.append(cart_item)
        elif cart_item['type'] == "Exotic_Fruits":
            Exotic_Fruits.append(cart_item)
        elif cart_item['type'] == "Vegetables":
            Vegetables.append(cart_item)
        elif cart_item['type'] == "Exotic_Vegetables":
            Exotic_Vegetables.append(cart_item)
        elif cart_item['type'] == "Foodgrains":
            Foodgrains.append(cart_item)
        else:
            print("Got Wrong Value")
    if len(Fruits) > 0:
        segragateDict["Fruits"] = Fruits
    if len(Exotic_Fruits) > 0:
        segragateDict["Exotic_Fruits"] = Exotic_Fruits
    if len(Vegetables) > 0:
        segragateDict["Vegetables"] = Vegetables
    if len(Exotic_Vegetables) > 0:
        segragateDict["Exotic_Vegetables"] = Exotic_Vegetables
    if len(Foodgrains) > 0:
        segragateDict["Foodgrains"] = Foodgrains
    return segragateDict

def getUpdateDict(seg_dict):
    updateDict = {}
    typeList = list(segDict.keys())
    for type in typeList:
        itemList = segDict[key]
        for item in itemList:
            updateDict['{}/{}/Available Quantity']



def updateDatabaseTransaction(cart_list):
    segDict = segragateCart(cart_list)
    updateDict = getUpdateDict(segDict)







@app.route("/hello", methods = ['POST','GET'])
def hello():
    tdict = {}
    jsonString = request.form['cartList']
    emailHash = request.form['emailHash']
    cartList = list(json.loads(jsonString).values())
    print(cartList)
    print(emailHash)
    currentOrderFlag = checkForCurrentOrder(emailHash)
    #check for Pending order
    if currentOrderFlag:
        tdict['message'] = "You have a pending order"
        return jsonify(tdict)
        #No Pending Order
    else:
        #Executng transaction
        updateDatabaseTransaction(cartList)






    tdict['hello'] = "hello from python"
    return jsonify(tdict)


if __name__ == "__main__":
    app.run(host='192.168.29.242', port=5000, debug = True )
