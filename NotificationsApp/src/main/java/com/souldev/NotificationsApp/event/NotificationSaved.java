package com.souldev.NotificationsApp.event;

import com.souldev.NotificationsApp.model.Notification;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NotificationSaved implements Event{
    private Notification notification;
}
