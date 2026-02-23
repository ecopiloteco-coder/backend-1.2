const mongoose = require('mongoose');

const EventSchema = new mongoose.Schema({
    action: {
        type: String,
        required: true
    },
    metadata: {
        type: Object
    },
    userId: {
        type: String
    },
    entityId: {
        type: String
    },
    serviceSource: {
        type: String
    },
    timestamp: {
        type: Date,
        default: Date.now,
        index: { expires: '90d' } // TTL Index: Auto delete after 90 days
    }
});

module.exports = mongoose.model('Event', EventSchema);
