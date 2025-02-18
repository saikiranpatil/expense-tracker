from flask import Flask, request, jsonify
from service.messageService import MessageService

app = Flask(__name__)
app.config.from_pyfile('config.py')

@app.route('/ds/v1/message', methods=['POST'])
def handleMessage():
    message = request.json.get('message')
    
    if message is None:
        return jsonify({"error": "message is required"}), 400
    
    response = MessageService().process_message(message)
    
    if response is None:
        return jsonify({"message": "Not an expense"}), 404
    
    return response.model_dump_json(exclude_unset=False)

@app.route('/', methods=['GET'])
def handleMessages():
    return "HELLO WORLD!"

if __name__ == '__main__':
    app.run(host="localhost", port=3000, debug=True)