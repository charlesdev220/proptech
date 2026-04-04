package com.proptech.backend.domain.service;

import com.proptech.backend.api.dto.PropertyDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendReviewInvitation(String toEmail, java.util.UUID reviewToken,
                                     String toUserName, String eventType,
                                     java.time.LocalDateTime expiresAt) {
        try {
            Context context = new Context();
            context.setVariable("toUserName", toUserName);
            context.setVariable("eventType", eventType);
            context.setVariable("reviewToken", reviewToken.toString());
            context.setVariable("frontendUrl", frontendUrl);
            context.setVariable("expiresAt", expiresAt);

            String htmlContent = templateEngine.process("email/review-invitation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Te invitamos a valorar tu experiencia con " + toUserName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send review invitation to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendSavedSearchAlert(String toEmail, String searchName, List<PropertyDTO> matches) {
        try {
            Context context = new Context();
            context.setVariable("searchName", searchName);
            context.setVariable("properties", matches);
            context.setVariable("frontendUrl", frontendUrl);

            String htmlContent = templateEngine.process("email/saved-search-alert", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Nuevas propiedades para tu búsqueda: " + searchName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
