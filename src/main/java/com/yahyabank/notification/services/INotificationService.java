package com.yahyabank.notification.services;

import com.yahyabank.auth_users.entity.User;
import com.yahyabank.notification.dtos.NotificationDTO;

public interface INotificationService {

    void sendEmail(NotificationDTO notificationDTO, User user);


}
