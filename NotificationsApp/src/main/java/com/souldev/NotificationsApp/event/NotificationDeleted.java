package com.souldev.NotificationsApp.event;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotificationDeleted implements Event{
    private String id;
}
