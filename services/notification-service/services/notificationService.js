const Notification = require('../models/Notification');

class NotificationService {
    /**
     * Create a new notification
     * @param {Object} data - Notification data
     * @returns {Promise<Object>} Created notification
     */
    async createNotification(data) {
        if (!data.userId) {
            throw new Error('userId is required');
        }
        return await Notification.create(data);
    }

    /**
     * Get all notifications for a user
     * @param {String} userId - User ID
     * @returns {Promise<Array>} List of notifications
     */
    async getUserNotifications(userId) {
        return await Notification.find({ userId: userId })
            .sort({ createdAt: -1 });
    }

    /**
     * Mark a notification as read
     * @param {String} id - Notification ID
     * @returns {Promise<Object>} Updated notification
     */
    async markAsRead(id) {
        const notification = await Notification.findById(id);

        if (!notification) {
            throw new Error('Notification not found');
        }
        notification.isRead = true;
        return await notification.save();
    }

    /**
     * Mark a notification as unread
     * @param {String} id - Notification ID
     * @returns {Promise<Object>} Updated notification
     */
    async markAsUnread(id) {
        const notification = await Notification.findById(id);
        if (!notification) {
            throw new Error('Notification not found');
        }
        notification.isRead = false;
        return await notification.save();
    }

    /**
     * Delete a notification
     * @param {String} id - Notification ID
     * @returns {Promise<void>}
     */
    async deleteNotification(id) {
        const notification = await Notification.findById(id);
        if (!notification) {
            throw new Error('Notification not found');
        }
        await notification.deleteOne();
    }

    /**
     * Get unread notification count
     * @param {String} userId - User ID
     * @returns {Promise<Number>} Count of unread notifications
     */
    async getUnreadCount(userId) {
        return await Notification.countDocuments({
            userId: userId, isRead: false
        });
    }
}

module.exports = new NotificationService();
