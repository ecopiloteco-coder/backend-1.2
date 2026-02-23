const mongoose = require('mongoose');

const NotificationSchema = new mongoose.Schema({
    userId: {
        type: String,
        required: true,
        index: true
    },
    subject: {
        type: String,
        required: true
    },
    content: {
        type: String,
        required: true
    },
    type: {
        type: String, // INFO, WARNING, SUCCESS, ERROR
        default: 'INFO'
    },
    isRead: {
        type: Boolean,
        default: false
    },
    createdAt: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Notification', NotificationSchema);
