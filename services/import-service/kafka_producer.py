from kafka import KafkaProducer
import json
import os

KAFKA_BROKER = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")

def get_producer():
    try:
        producer = KafkaProducer(
            bootstrap_servers=KAFKA_BROKER,
            value_serializer=lambda v: json.dumps(v).encode('utf-8')
        )
        return producer
    except Exception as e:
        print(f"Error connecting to Kafka: {e}")
        return None

producer = get_producer()

def send_kafka_event(topic, data):
    if producer:
        producer.send(topic, data)
        producer.flush()
        print(f"Sent event to {topic}: {data}")
    else:
        print("Kafka producer not available")
