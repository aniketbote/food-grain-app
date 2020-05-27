from flask import Flask, jsonify, request
import json
import re
from pprint import pprint
import os
import random
import string
from datetime import date
import time
from pprint import pprint
#Firebase realtime database
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cartList = []
errorCode = 0
itemDeficiency = ''

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Desktop/Aniket/food-grain-app/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})

combinedItemsRef = db.reference('combined_items')
combinedItems = combinedItemsRef.get()
combinedItemsKeys = list(combinedItems.keys())


app = Flask(__name__)


def generateOrderId():
    chars = string.ascii_uppercase + string.digits
    return ''.join(random.choice(chars) for _ in range(5))

def updateData(current_value, cart_list, email_hash):
    orderDict = {}
    itemDict = {}
    tempalldict = {}
    orderId = generateOrderId()
    totalAmount = 0
    global errorCode
    global itemDeficiency
    errorCode = 0
    itemDeficiency = ''

    for item in cart_list:
        tdict = {}
        tdict['Count'] = item['count']
        tdict['Image'] = item['image']
        tdict['Amount'] = str(int(item['price']) * int(item['count']))
        totalAmount += int(tdict['Amount'])
        tempalldict[item['name']] = tdict

        tempAQ = current_value[item['type']][item['name']]['Available Quantity']
        if (int(tempAQ) - int(item['count'])) >= 0:
            current_value[item['type']][item['name']]['Available Quantity'] = str(int(tempAQ) - int(item['count']))
        else:
            errorCode = 1
            itemDeficiency = itemDeficiency + item['name'] + ','
    if errorCode == 1:
        raise db.TransactionAbortedError("Transaction Failed")

    itemDict['Items'] = tempalldict
    itemDict['Date of Order'] = date.today().strftime('%Y-%m-%d')
    itemDict['Date of Completion'] = "PENDING"
    itemDict['Total'] = totalAmount
    orderDict[orderId] = itemDict

    try:
        orderRef = db.reference('all_orders/{}/current'.format(email_hash))
        orderRef.set(orderDict)
    except db.FirebaseError:
        errorCode = 2
        raise db.TransactionAbortedError("Transaction Failed")

    # pprint(orderDict)
    return current_value

def transactionOp(current_value):
    new_value = updateData(current_value, cartList, emhash_global)
    return current_value








@app.route("/placeorder", methods = ['POST','GET'])
def placeorder():
    global cartList
    global emhash_global
    tdict = {}
    jsonString = request.form['cartList']
    emailHash = request.form['emailHash']
    print(emailHash)
    print('\n')
    try:
        tranRef = db.reference('all_items')
        cartList = list(json.loads(jsonString).values())
        emhash_global = emailHash
        new_transRef = tranRef.transaction(transactionOp)
        print("Transaction Completed")
        tdict['message'] = "Ordered Successfully"
        tdict['deficiency'] = ''
        tdict['errorCode'] = errorCode
    except db.TransactionAbortedError:
        if errorCode == 1:
            tdict['message'] = "Selected Items Unavailable"
            tdict['deficiency'] = itemDeficiency
            tdict['errorCode'] = errorCode
        elif errorCode == 2:
            tdict['message'] = "Some Error Ocuured"
            tdict['deficiency'] = ''
            tdict['errorCode'] = errorCode
        elif errorCode == 0:
            tdict['message'] = "Some Error Ocuured Try again later"
            tdict['deficiency'] = ''
            tdict['errorCode'] = 3
        print("Transaction Failed")

    return jsonify(tdict)

@app.route("/orderreceived", methods = ['POST','GET'])
def orderreceived():
    try:
        responseDict = {}
        emailHash = request.form['emailHash']
        currentref = db.reference('all_orders/{}/current'.format(emailHash))
        orderData = currentref.get()
        currentref.set({})
        orderId = list(orderData.keys())[0]
        itemDict = list(orderData.values())[0]
        itemDict['Date of Completion'] = date.today().strftime('%Y-%m-%d')
        previousref = db.reference('all_orders/{}/previous/{}'.format(emailHash, orderId))
        previousref.set(itemDict)
        responseDict['message'] = "Thank You for your Response"
        responseDict['errorCode'] = 0

    except:
        responseDict['message'] = "Some error Occured. Try again later"
        responseDict['errorCode'] = 2
    return jsonify(responseDict)

@app.route("/search", methods = ['POST','GET'])
def search():
    responseDict = {}
    searchItemList = []
    pattern = request.form['pattern']
    print(pattern)
    matched1 = [x for x in combinedItemsKeys if re.search("^{}".format(pattern.lower()), x.lower())]
    matched2 = [x for x in combinedItemsKeys if (re.search("{}".format(pattern.lower()), x.lower()) and x not in matched1) ]
    finalMatched = matched1 + matched2
    if len(finalMatched)==len(combinedItems):
        finalMatched = []
    for item in finalMatched:
        tempDict = {}
        tempDict['name'] = item
        tempDict['description'] = combinedItems[item]['Description']
        tempDict['imageUrl'] = combinedItems[item]['Image']
        tempDict['size'] = combinedItems[item]['Size']
        tempDict['price'] = combinedItems[item]['Price']
        tempDict['availableQuantity'] = combinedItems[item]['Available Quantity']
        tempDict['type'] = combinedItems[item]['Type']
        searchItemList.append(tempDict)
    responseDict['itemList'] = searchItemList
    return jsonify(responseDict)



if __name__ == "__main__":
    app.run(host='192.168.0.3', port=5000, debug = True )
    #app.run(host='192.168.29.242', port=5000, debug = True )
