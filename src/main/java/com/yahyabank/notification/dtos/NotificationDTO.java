package com.yahyabank.notification.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yahyabank.auth_users.entity.User;
import com.yahyabank.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {


    private Long id;

    private String subject;

    private String recipient;

    private String body;


    private NotificationType type;


    private User user;

    private LocalDateTime createdAt;


    private String templateName;

    private Map<String,Object> templateVariables;












}
