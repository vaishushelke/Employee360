

package com.employee360.backend.services;

import java.util.List;

import com.employee360.backend.dto.response.NotificationResponse;

public interface NotificationService {

    // Get all notifications for the logged-in employee
    List<NotificationResponse> getMyNotifications(String username);

    // Get unread notifications
    List<NotificationResponse> getMyUnreadNotifications(String username);

    // Get unread notification count
    long getUnreadCount(String username);

    // Mark one notification as read
    NotificationResponse markAsRead(Long notificationId, String username);

    // Mark all notifications as read
    void markAllAsRead(String username);

    // Create a notification for an employee
    NotificationResponse createNotification(
            String recipientUsername,
            String title,
            String message,
            String type);
}

