package com.yahyabank.notification.services;

import com.yahyabank.auth_users.entity.User;
import com.yahyabank.notification.dtos.NotificationDTO;
import com.yahyabank.notification.repo.NotificationRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
//@EnableCaching
public class NotificationService implements INotificationService {

    private final NotificationRepo notificationRepo;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name()
            );


            //use template if provided

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());
            if (notificationDTO.getTemplateName() != null) {
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());
                String htmlContext = templateEngine.process(notificationDTO.getTemplateName(), context);
                helper.setText(htmlContext, true);

                //if no template send text body directly

            } else {

                helper.setText(notificationDTO.getBody(), true);
            }

            mailSender.send(mimeMessage);
            log.info("Email send Out");

            //save it to database table


//            Notification notificationToSave = Notification.builder()
//                    .recipient(notificationDTO.getRecipient())
//                    .subject(notificationDTO.getSubject())
//                    .body(notificationDTO.getBody())
//                    .type(NotificationType.EMAIL)
//                    .user(user)
//                    .build();
//
//            notificationRepo.save(notificationToSave);

        } catch (MessagingException e) {
            log.error(e.getMessage());
        }

    }
}
