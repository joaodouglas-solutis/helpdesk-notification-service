package com.solutis.notificationservice.repository;

import com.solutis.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByCustomerIdAndActiveTrue(UUID customerId);

    List<Notification> findByActiveTrue();

    @Modifying
    @Query("""
            update Notification n
            set n.active = false
            where n.customerId = :customerId
              and n.active = true
            """)
    int deactivateByCustomerId(
            @Param("customerId") UUID customerId
    );

    @Modifying
    @Query("""
            update Notification n
            set n.active = false
            where n.active = true
            """)
    int deactivateAll();
}