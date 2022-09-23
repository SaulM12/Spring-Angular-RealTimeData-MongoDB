package com.souldev.NotificationsApp.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Getter
@Setter
public class Notification {
    @Id
    private String id;
    private String message;
    private String severity;

    public Notification(String message, String severity) {
        this.message = message;
        this.severity = severity;
    }
}
