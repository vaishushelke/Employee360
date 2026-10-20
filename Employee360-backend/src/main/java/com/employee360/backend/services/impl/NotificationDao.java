
package com.employee360.backend.services.impl;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.employee360.backend.dto.response.NotificationResponse;
import com.employee360.backend.entities.Notification;
import com.employee360.backend.enums.NotificationType;
import com.employee360.backend.repos.NotificationRepo;
import com.employee360.backend.services.NotificationService;

@Service
@Transactional
public class NotificationDao implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Override
    public List<NotificationResponse> getMyNotifications(String username) {

        return notificationRepo
                .findByRecipientUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getMyUnreadNotifications(String username) {

        return notificationRepo
                .findByRecipientUsernameAndReadFalseOrderByCreatedAtDesc(username)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(String username) {

        return notificationRepo.countByRecipientUsernameAndReadFalse(username);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId, String username) {

        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"));

        if (!notification.getRecipientUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot access another employee's notification");
        }

        notification.setRead(true);

        return convertToResponse(notificationRepo.save(notification));
    }

    @Override
    public void markAllAsRead(String username) {

        List<Notification> notifications =
                notificationRepo
                        .findByRecipientUsernameAndReadFalseOrderByCreatedAtDesc(username);

        for (Notification notification : notifications) {
            notification.setRead(true);
        }

        notificationRepo.saveAll(notifications);
    }

    @Override
    public NotificationResponse createNotification(
            String recipientUsername,
            String title,
            String message,
            String type) {

        if (recipientUsername == null || recipientUsername.isBlank()
                || title == null || title.isBlank()
                || message == null || message.isBlank()
                || type == null || type.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Recipient, title, message and type are required");
        }

        NotificationType notificationType;

        try {
            notificationType = NotificationType.valueOf(
                    type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid notification type. Allowed types: INFO, SUCCESS, WARNING, ERROR");
        }

        Notification notification = new Notification(
                recipientUsername.trim(),
                title.trim(),
                message.trim(),
                notificationType);

        return convertToResponse(notificationRepo.save(notification));
    }

    private NotificationResponse convertToResponse(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType().name(),
                notification.isRead(),
                notification.getCreatedAt());
    }
}
