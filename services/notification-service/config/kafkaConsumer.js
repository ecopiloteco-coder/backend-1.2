const kafka = require('../config/kafka');
const NotificationFactory = require('../services/NotificationFactory');

const startConsumer = async () => {
    const consumer = kafka.consumer({ groupId: 'notification-group' });

    await consumer.connect();
    // Subscribe to multiple topics
    await consumer.subscribe({ topics: ['user.events', 'article.events', 'project.events', 'import.jobs'], fromBeginning: true });

    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            const prefix = `${topic}[${partition}|${message.offset}] / ${message.timestamp}`;
            const payloadStr = message.value.toString();
            console.log(`- ${prefix} ${payloadStr}`);

            try {
                const event = JSON.parse(payloadStr);
                
                // Use Factory Pattern to handle notification creation
                // Defaulting to 'in-app' for all Kafka events for now
                const notification = NotificationFactory.createNotification('in-app', event.userId || 'unknown', `Received event: ${event.action}`);
                notification.send();

            } catch (e) {
                console.error("Error processing Kafka message", e);
            }
        },
    });
};

module.exports = startConsumer;
