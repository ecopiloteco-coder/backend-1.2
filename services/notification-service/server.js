require('dotenv').config();
const express = require('express');
const http = require('http');
const connectDB = require('./config/db');
const { initKafkaConsumer } = require('./config/kafka');
const { initSocket } = require('./services/socketService');
const notificationRoutes = require('./routes/notificationRoutes');
const eventRoutes = require('./routes/eventRoutes');

const app = express();
const server = http.createServer(app);

// Initialize Services
connectDB();
const io = initSocket(server);
initKafkaConsumer(io); // Pass socket instance to Kafka consumer

// Middleware
app.use(express.json());

// Routes
app.use('/api/notifications', notificationRoutes);
app.use('/api/events', eventRoutes);

const PORT = process.env.PORT || 8084;

server.listen(PORT, () => {
    console.log(`Notification Service running on port ${PORT}`);
});
