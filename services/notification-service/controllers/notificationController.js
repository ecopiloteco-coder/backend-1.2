const Notification = require('../models/Notification');

// @desc    Get all notifications for a user
// @route   GET /api/notifications
// @access  Private (User ID from header X-Auth-User-Id injected by Gateway or query param)
exports.getNotifications = async (req, res) => {
    try {
        // Try getting userId from header (Gateway) or query param (Direct Call)
        const userId = req.header('X-Auth-User-Id') || req.query.userId;
        
        if (!userId) {
            return res.status(400).json({ success: false, message: 'User ID missing' });
        }

        const limit = parseInt(req.query.limit) || 10;
        const notifications = await Notification.find({ userId: userId })
            .sort({ createdAt: -1 })
            .limit(limit);

        res.status(200).json({
            success: true,
            count: notifications.length,
            data: notifications
        });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
};

// @desc    Mark notification as read
// @route   PUT /api/notifications/:id/read
exports.markAsRead = async (req, res) => {
    try {
        let notification = await Notification.findById(req.params.id);

        if (!notification) {
            return res.status(404).json({ success: false, message: 'Notification not found' });
        }

        // Verify user owns notification
        const userId = req.header('X-Auth-User-Id') || req.body.userId; // Support body userId for direct calls
        if (userId && notification.userId.toString() !== userId.toString()) {
            return res.status(403).json({ success: false, message: 'Not authorized' });
        }

        notification = await Notification.findByIdAndUpdate(req.params.id, { isRead: true }, { new: true });

        res.status(200).json({ success: true, data: notification });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
};

// @desc    Mark ALL notifications as read
// @route   PUT /api/notifications/read-all
exports.markAllAsRead = async (req, res) => {
    try {
        const userId = req.header('X-Auth-User-Id') || req.body.userId; // Support body userId for direct calls
        if (!userId) {
            return res.status(400).json({ success: false, message: 'User ID missing' });
        }

        await Notification.updateMany({ userId: userId, isRead: false }, { isRead: true });

        res.status(200).json({ success: true, message: 'All notifications marked as read' });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
};

// @desc    Delete notification
// @route   DELETE /api/notifications/:id
exports.deleteNotification = async (req, res) => {
    try {
        const notification = await Notification.findById(req.params.id);

        if (!notification) {
            return res.status(404).json({ success: false, message: 'Notification not found' });
        }

        // Verify user owns notification
        const userId = req.header('X-Auth-User-Id') || req.query.userId || req.body.userId; // Support query/body userId
        if (userId && notification.userId.toString() !== userId.toString()) {
            return res.status(403).json({ success: false, message: 'Not authorized' });
        }

        await notification.deleteOne();

        res.status(200).json({ success: true, data: {} });
    } catch (err) {
        res.status(500).json({ success: false, error: err.message });
    }
};
