const { Kafka } = require('kafkajs');
const Event = require('../models/Event');
const Notification = require('../models/Notification');
const NotificationFactory = require('../services/NotificationFactory');

const kafka = new Kafka({
    clientId: 'notification-service',
    brokers: [(process.env.KAFKA_BOOTSTRAP_SERVERS || 'localhost:9092')]
});

const consumer = kafka.consumer({ groupId: 'notification-group' });

const initKafkaConsumer = async (io) => {
    const connect = async () => {
        try {
            console.log('Connecting to Kafka...');
            await consumer.connect();
            console.log('Kafka Consumer Connected Successfully');

            // Subscribe to all relevant topics
            await consumer.subscribe({ topics: ['user.events', 'article.events', 'project.events', 'import.jobs'], fromBeginning: false });

            await consumer.run({
                eachMessage: async ({ topic, partition, message }) => {
                    const payload = JSON.parse(message.value.toString());
                    console.log(`Received message on ${topic}:`, payload);

                    // 1. Audit Log (Event)
                    try {
                        let entityId = payload.id || null;
                        if (!entityId) {
                            if (topic === 'project.events') entityId = payload.projectId;
                            else if (topic === 'article.events') entityId = payload.articleId;
                            else if (topic === 'user.events') entityId = payload.userId;
                        }

                        // Use only business metadata when available (payload.metadata),
                        // otherwise fallback to the whole payload.
                        let metadata = payload;
                        if (payload && typeof payload === 'object' && payload.metadata && typeof payload.metadata === 'object') {
                            metadata = payload.metadata;
                        }

                        const event = new Event({
                            action: payload.action || payload.eventType || 'UNKNOWN_ACTION',
                            metadata,
                            userId: payload.userId || payload.user_id, // Normalize
                            entityId: entityId ? entityId.toString() : null,
                            serviceSource: topic.split('.')[0]
                        });
                        await event.save();

                        // 2. Notification Logic (Simplified rule: Notify all or specific users)
                        // In real app: lookup recipients based on payload logic
                        const recipientId = (payload.keycloakId || payload.userId || payload.user_id)?.toString();

                        if (recipientId) {
                            // Use Factory Pattern to create and handle notification
                            // We default to 'in-app' type for standard events
                            const notificationProduct = NotificationFactory.createNotification('in-app', recipientId, `Event ${payload.action || payload.eventType} occurred on ${topic}`);
                            notificationProduct.send(); // Logs to console

                            // Persist to DB
                            const notif = new Notification({
                                userId: recipientId,
                                content: payload.content || notificationProduct.message,
                                type: payload.type || 'INFO',
                                subject: payload.subject || 'Notification Système'
                            });
                            await notif.save();

                            // 3. Real-time Push via Socket.io
                            io.to(recipientId).emit('notification', notif);
                        }

                    } catch (err) {
                        console.error('Error processing Kafka message:', err);
                    }
                },
            });
        } catch (error) {
            console.error('Error connecting to Kafka:', error.message);
            if (error.type === 'KafkaJSGroupCoordinatorNotFound' || error.name === 'KafkaJSGroupCoordinatorNotFound') {
                console.log('Group Coordinator not found, retrying in 3 seconds...');
                setTimeout(connect, 3000);
            } else {
                console.log('Retrying Kafka connection in 5 seconds...');
                setTimeout(connect, 5000);
            }
        }
    };

    connect();
};

module.exports = { initKafkaConsumer };
