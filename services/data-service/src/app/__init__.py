from flask import Flask, request, jsonify
from service.messageService import MessageService
from utils.configUtil import ConfigUtil
from kafka import KafkaProducer

import json

app = Flask(__name__)
app.config.from_pyfile('config.py')

messageService = MessageService()
producer = KafkaProducer(
        bootstrap_servers=[ConfigUtil.getKafkaUrl()], 
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

@app.route('/ds/v1/message', methods=['POST'])
def handleMessage():
    message = request.json.get('message')
    
    if message is None:
        return jsonify({"error": "message is required"}), 400
    
    response = messageService.process_message(message)
    
    if response is None:
        return jsonify({"message": "Not an expense"}), 404
    
    response_json = response.model_dump_json(exclude_unset=False)
    producer.send("expense-service-topic", json.loads(response_json))
    
    return response_json

@app.route('/ds/v1/health', methods=['GET'])
def helloWorld():
    return "HELLO WORLD!"

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=9881, debug=True)