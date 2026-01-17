package com.yahyabank;

import com.yahyabank.auth_users.entity.User;
import com.yahyabank.enums.NotificationType;
import com.yahyabank.notification.dtos.NotificationDTO;
import com.yahyabank.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class YahyabankApplication {

    private final NotificationService notificationService;

    public static void main(String[] args) {
        SpringApplication.run(YahyabankApplication.class, args);
    }

//    @Bean
//    CommandLineRunner runner() {
//
//        return args -> {
//            NotificationDTO notificationDTO = NotificationDTO.builder()
//                    .recipient("salahelmotwakel@gmail.com")
//                    .subject("tasting email")
//                    .body("this is testing email")
//                    .type(NotificationType.EMAIL)
//                    .build();
//            notificationService.sendEmail(notificationDTO, new User());
//        };
//    }


}
