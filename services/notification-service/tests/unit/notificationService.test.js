const notificationService = require('../../services/notificationService');
const Notification = require('../../models/Notification');

jest.mock('../../models/Notification');

/**
 * Unit tests for NotificationService
 * 
 * Tests cover:
 * - Create notifications
 * - Mark as read/unread
 * - Get user notifications
 * - Delete notifications
 */
describe('NotificationService', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('createNotification', () => {
        it('should create notification successfully', async () => {
            // Arrange
            const notificationData = {
                userId: 1,
                message: 'New project created',
                type: 'project_created'
            };

            const mockNotification = {
                id: 1,
                ...notificationData,
                isRead: false,
                createdAt: new Date()
            };

            Notification.create.mockResolvedValue(mockNotification);

            // Act
            const result = await notificationService.createNotification(notificationData);

            // Assert
            expect(result.id).toBe(1);
            expect(result.isRead).toBe(false);
            expect(result.type).toBe('project_created');
            expect(Notification.create).toHaveBeenCalledWith(notificationData);
        });

        it('should throw error when required fields are missing', async () => {
            // Arrange
            const invalidData = { message: 'Test' }; // Missing userId

            // Act & Assert
            await expect(notificationService.createNotification(invalidData))
                .rejects.toThrow('userId is required');
        });
    });

    describe('getUserNotifications', () => {
        it('should return all notifications for user', async () => {
            // Arrange
            const mockNotifications = [
                { id: 1, userId: 1, message: 'Test 1', isRead: false },
                { id: 2, userId: 1, message: 'Test 2', isRead: true }
            ];

            Notification.find.mockReturnValue({
                sort: jest.fn().mockResolvedValue(mockNotifications)
            });

            // Act
            const result = await notificationService.getUserNotifications(1);

            // Assert
            expect(result).toHaveLength(2);
            expect(Notification.find).toHaveBeenCalledWith({ userId: 1 });
        });

        it('should return empty array when no notifications found', async () => {
            // Arrange
            Notification.find.mockReturnValue({
                sort: jest.fn().mockResolvedValue([])
            });

            // Act
            const result = await notificationService.getUserNotifications(999);

            // Assert
            expect(result).toEqual([]);
        });
    });

    describe('markAsRead', () => {
        it('should mark notification as read', async () => {
            // Arrange
            const mockNotification = {
                id: 1,
                isRead: false,
                save: jest.fn().mockResolvedValue({ id: 1, isRead: true })
            };

            Notification.findById.mockResolvedValue(mockNotification);

            // Act
            await notificationService.markAsRead(1);

            // Assert
            expect(mockNotification.save).toHaveBeenCalled();
        });

        it('should throw error when notification not found', async () => {
            // Arrange
            Notification.findById.mockResolvedValue(null);

            // Act & Assert
            await expect(notificationService.markAsRead(999))
                .rejects.toThrow('Notification not found');
        });
    });

    describe('markAsUnread', () => {
        it('should mark notification as unread', async () => {
            // Arrange
            const mockNotification = {
                id: 1,
                isRead: true,
                save: jest.fn().mockResolvedValue({ id: 1, isRead: false })
            };

            Notification.findById.mockResolvedValue(mockNotification);

            // Act
            await notificationService.markAsUnread(1);

            // Assert
            expect(mockNotification.save).toHaveBeenCalled();
        });
    });

    describe('deleteNotification', () => {
        it('should delete notification successfully', async () => {
            // Arrange
            const mockNotification = {
                id: 1,
                deleteOne: jest.fn().mockResolvedValue()
            };

            Notification.findById.mockResolvedValue(mockNotification);

            // Act
            await notificationService.deleteNotification(1);

            // Assert
            expect(mockNotification.deleteOne).toHaveBeenCalled();
        });

        it('should throw error when deleting non-existent notification', async () => {
            // Arrange
            Notification.findById.mockResolvedValue(null);

            // Act & Assert
            await expect(notificationService.deleteNotification(999))
                .rejects.toThrow('Notification not found');
        });
    });

    describe('getUnreadCount', () => {
        it('should return count of unread notifications', async () => {
            // Arrange
            Notification.countDocuments.mockResolvedValue(5);

            // Act
            const count = await notificationService.getUnreadCount(1);

            // Assert
            expect(count).toBe(5);
            expect(Notification.countDocuments).toHaveBeenCalledWith({
                userId: 1, isRead: false
            });
        });

        it('should return 0 when no unread notifications', async () => {
            // Arrange
            Notification.countDocuments.mockResolvedValue(0);

            // Act
            const count = await notificationService.getUnreadCount(1);

            // Assert
            expect(count).toBe(0);
        });
    });
});
