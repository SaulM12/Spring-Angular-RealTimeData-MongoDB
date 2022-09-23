package com.souldev.NotificationsApp.repository;

import com.souldev.NotificationsApp.model.Notification;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {
}
