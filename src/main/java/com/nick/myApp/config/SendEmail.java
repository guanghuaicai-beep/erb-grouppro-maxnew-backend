package com.nick.myApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SendEmail {
    
   private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:https://your-frontend-domain.com}")
    private String frontendUrl;

    public SendEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Reset password");
        message.setText("Please click the link to reset your password (valid for 30 minutes):\n" + resetLink);
        mailSender.send(message);
    }

    public void sendPasswordUpdatedNotification(String to) {
    

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Reset password successfully");
        message.setText("Your password has been updated");

        mailSender.send(message);
    }
 
 
}
