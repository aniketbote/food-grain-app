from flask import Flask, jsonify, request
import json
from pprint import pprint

app = Flask(__name__)



@app.route("/hello", methods = ['POST','GET'])
def hello():
    jsonString = request.form['cartList']
    cartList = list(json.loads(jsonString).values())
    print(cartList[0]['name'])
    tdict = {}
    tdict['hello'] = "hello from python"
    return jsonify(tdict)


if __name__ == "__main__":
    app.run(host='192.168.29.242', port=5000, debug = True )
