package com.proptech.backend.domain.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    void sendSavedSearchAlert_doesNotPropagateSMTPExceptions() {
        // Arrange
        String to = "test@example.com";
        String name = "Test Search";

        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html></html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Simulate SMTP failure
        doThrow(new RuntimeException("SMTP failed")).when(mailSender).send(any(MimeMessage.class));

        // Act
        emailNotificationService.sendSavedSearchAlert(to, name, Collections.emptyList());

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
        // No exception should be thrown to the caller
    }
}
