
package com.employee360.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee360.backend.dto.response.NotificationResponse;
import com.employee360.backend.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get all notifications for the logged-in user
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                notificationService.getMyNotifications(username));
    }

    // Get unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getMyUnreadNotifications(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(username));
    }

    // Get unread notification count for dashboard badge
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                notificationService.getUnreadCount(username));
    }

    // Mark one notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId, username));
    }

    // Mark all notifications as read
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(
            Authentication authentication) {

        String username = authentication.getName();

        notificationService.markAllAsRead(username);

        return ResponseEntity.noContent().build();
    }
}
