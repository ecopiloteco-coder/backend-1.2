const socketIo = require('socket.io');

let io;

const initSocket = (server) => {
    io = socketIo(server, {
        cors: {
            origin: "*", // Adjust for specific frontend URL in prod
            methods: ["GET", "POST"]
        }
    });

    io.on('connection', (socket) => {
        console.log('New client connected:', socket.id);

        // Join room based on User ID for targeted notifications
        socket.on('join', (userId) => {
            socket.join(userId);
            console.log(`User ${userId} joined room`);
        });

        socket.on('disconnect', () => {
            console.log('Client disconnected');
        });
    });

    return io;
};

module.exports = { initSocket };
