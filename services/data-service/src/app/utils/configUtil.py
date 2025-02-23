import os

class ConfigUtil:
    @staticmethod
    def getKafkaUrl():
        kafka_host = os.getenv("KAFKA_HOST", "localhost")
        kafka_port = os.getenv("KAFKA_PORT", "9092")
        kafka_url = kafka_host + ":" + kafka_port
        
        print("Kafka starting at: ", kafka_url, "\n")
        
        return kafka_url