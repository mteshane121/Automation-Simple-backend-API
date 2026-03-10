package com.operaton.service;

import com.operaton.model.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * SERVICE — Handles sending emails via Gmail.
 * Uses Spring Boot's built-in mail support.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a welcome email to the newly created user.
     */
    public void sendWelcomeEmail(UserRequest user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("gumteshane@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Welcome to the Team, " + user.getFullName() + "! 🎉");
            message.setText(
                "Hi " + user.getFullName() + ",\n\n" +
                "Welcome aboard! Your account has been approved and created.\n\n" +
                "Your details:\n" +
                "  Full Name:  " + user.getFullName() + "\n" +
                "  Email:      " + user.getEmail() + "\n" +
                "  Department: " + user.getDepartment() + "\n" +
                "  Role:       " + user.getRole() + "\n\n" +
                "You can now log in and get started.\n\n" +
                "Best regards,\n" +
                "The Operaton BPM System\n" +
                "Powered by Spring Boot + Operaton"
            );

            mailSender.send(message);
            System.out.println(" Welcome email sent to: " + user.getEmail());

        } catch (Exception e) {
            System.out.println(" Email failed: " + e.getMessage());
        }
    }
}