
package com.employee360.backend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee360.backend.entities.Notification;

@Repository
public interface NotificationRepo
        extends JpaRepository<Notification, Long> {

    // Get all notifications for one employee
    List<Notification> findByRecipientUsernameOrderByCreatedAtDesc(
            String recipientUsername);

    // Get unread notifications for one employee
    List<Notification> findByRecipientUsernameAndReadFalseOrderByCreatedAtDesc(
            String recipientUsername);

    // Count unread notifications for one employee
    long countByRecipientUsernameAndReadFalse(
            String recipientUsername);
}

