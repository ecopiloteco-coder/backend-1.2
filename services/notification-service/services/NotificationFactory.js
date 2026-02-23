// Factory Pattern Implementation for Notifications

class NotificationProduct {
    constructor(userId, message) {
        this.userId = userId;
        this.message = message;
    }
    send() {
        throw new Error("Method 'send()' must be implemented.");
    }
}

class InAppNotification extends NotificationProduct {
    send() {
        console.log(`[In-App] Saving notification for user ${this.userId}: ${this.message}`);
        // Logic to save to MongoDB 'notifications' collection would go here
        // or triggering Socket.IO event
    }
}

class EmailNotification extends NotificationProduct {
    send() {
        console.log(`[Email] Sending email to user ${this.userId}: ${this.message}`);
        // Logic to send email (SMTP/SendGrid)
    }
}

class NotificationFactory {
    static createNotification(type, userId, message) {
        switch (type) {
            case 'in-app':
                return new InAppNotification(userId, message);
            case 'email':
                return new EmailNotification(userId, message);
            default:
                throw new Error(`Unknown notification type: ${type}`);
        }
    }
}

module.exports = NotificationFactory;
