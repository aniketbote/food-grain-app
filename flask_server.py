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
import time
#Firebase realtime database
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

#Braintree
import braintree

gateway = braintree.BraintreeGateway(
  braintree.Configuration(
    environment=braintree.Environment.Sandbox,
    merchant_id='c9zswznhcgy499sv',
    public_key='h32bbxg6wckmx83w',
    private_key='ca34384614ba3f669b861e9d5c2d1dc4'
  )
)


cartList = []
errorCode = 0
itemDeficiency = ''

os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = "C:/Users/Aniket/Documents/Aniket/food-grain-app/Credentials/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json"
cred = credentials.Certificate('Credentials/farmfresh-9c7fd-firebase-adminsdk-dx65j-9533ee02a1.json')

firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://farmfresh-9c7fd.firebaseio.com/'
})

def get_combinedItems():
    tempref = db.reference('all_items')
    combined_data = {}
    data = tempref.get()
    for category in data:
        combined_data.update(data[category])
    combined_keys = combined_data.keys()
    return combined_data, combined_keys

combinedItems, combinedItemsKeys = get_combinedItems()

app = Flask(__name__)
def checkForCurrentOrder(emHash):
    ref = db.reference('all_orders/{}/current'.format(emHash))
    if ref.get() == None:
        return True,"No Pending"
    else:
        return False,"Pending "


def generateOrderId():
    chars = string.ascii_uppercase + string.digits
    return ''.join(random.choice(chars) for _ in range(5))

def updateData(current_value, cart_list, email_hash, orderAddress):
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
        tempOC = current_value[item['type']][item['name']]['OrderCount']
        if (int(tempAQ) - int(item['count'])) >= 0:
            current_value[item['type']][item['name']]['Available Quantity'] = int(tempAQ) - int(item['count'])
            current_value[item['type']][item['name']]['OrderCount'] = int(tempOC) - int(item['count'])
        else:
            errorCode = 1
            itemDeficiency = itemDeficiency + item['name'] + ','
    if errorCode == 1:
        raise db.TransactionAbortedError("Transaction Failed")

    itemDict['Items'] = tempalldict
    itemDict['Date of Order'] = date.today().strftime('%Y-%m-%d')
    itemDict['Date of Completion'] = "PENDING"
    itemDict['Total'] = totalAmount
    itemDict['Address'] = orderAddress
    itemDict['OrderTime'] = int(time.time()) * -1
    orderDict[orderId] = itemDict

    try:
        orderRef = db.reference('all_orders/{}/current'.format(email_hash))
        orderRef.set(orderDict)
    except db.FirebaseError:
        errorCode = 2
        raise db.TransactionAbortedError("Transaction Failed")

    return current_value

def transactionOp(current_value):
    new_value = updateData(current_value, cartList, emhash_global, address)
    return new_value

def get_reponse(data):
    final_list = []
    count = 0
    for element in data:
        if count >= 10:
            break
        # if element[1]['Available Quantity'] == '0':
        #     continue
        tempDict = {}
        tempDict['name'] = element[0]
        tempDict['description'] = element[1]['Description']
        tempDict['imageUrl'] = element[1]['Image']
        tempDict['size'] = element[1]['Size']
        tempDict['price'] = element[1]['Price']
        tempDict['availableQuantity'] = element[1]['Available Quantity']
        tempDict['type'] = element[1]['Type']
        final_list.append(tempDict)
        count += 1
    return final_list






@app.route("/client_token", methods=["GET"])
def client_token():
    return jsonify(gateway.client_token.generate())

@app.route("/checkout", methods=["POST","GET"])
def create_purchase():
    responseDict = {}
    nonce_from_the_client = request.form["nonce"]
    amount = request.form["amount"]
    result = gateway.transaction.sale({
                        "amount": amount,
                        "payment_method_nonce": nonce_from_the_client,
                        "options": {"submit_for_settlement": True}
                        })
    if(re.search("SuccessfulResult",str(result))):
        responseDict['success'] = "true"
        responseDict['transaction_id'] = result.transaction.id
    else:
        responseDict['success'] = "false"
        responseDict['transaction_id'] = ""

    return jsonify(responseDict)



@app.route("/popular", methods = ['POST','GET'])
def popularItems():
    global combinedItems
    global combinedItemsKeys
    combinedItems, combinedItemsKeys = get_combinedItems()
    res = sorted(combinedItems.items(), key = lambda x: x[1]['OrderCount'])
    popularList = get_reponse(res)
    return jsonify(popularList)







@app.route("/placeorder", methods = ['POST','GET'])
def placeorder():
    global cartList
    global emhash_global
    global address
    global combinedItems
    global combinedItemsKeys
    tdict = {}
    jsonString = request.form['cartList']
    emailHash = request.form['emailHash']
    address = request.form['address']
    currentStatus, pendingMsg = checkForCurrentOrder(emailHash)
    if not currentStatus:
        tdict['message'] = pendingMsg
        tdict['deficiency'] = ''
        tdict['errorCode'] = 4
        return jsonify(tdict)

    print(emailHash)
    print('\n')
    try:
        tranRef = db.reference('all_items')
        cartList = list(json.loads(jsonString).values())
        emhash_global = emailHash
        new_transRef = tranRef.transaction(transactionOp)
        combinedItems, combinedItemsKeys = get_combinedItems()
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
    # app.run(host='192.168.0.3', port=5000, debug = True )
    app.run(host='192.168.29.242', port=5000, debug = True )
