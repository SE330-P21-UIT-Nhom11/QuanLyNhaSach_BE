package com.example.quanlynhasach.service;

import jakarta.mail.MessagingException;

/**
 * Service interface for email operations
 */
public interface EmailService {
    /**
     * Send HTML email
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content
     * @throws MessagingException if email sending fails
     */
    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;
    
    /**
     * Send password reset email
     * @param to Recipient email address
     * @param resetToken Reset token
     * @param userName User name
     */
    void sendPasswordResetEmail(String to, String resetToken);
    
    /**
     * Send welcome email to new user with account credentials
     * @param to Recipient email address
     * @param userName User name
     * @param userEmail User's email/username for login
     * @param temporaryPassword Temporary password generated for the user
     */
    void sendWelcomeEmail(String to, String userName, String userEmail, String temporaryPassword);
    
    /**
     * Send order confirmation email
     * @param to Recipient email address
     * @param orderNumber Order number
     * @param userName User name
     */
    void sendOrderConfirmationEmail(String to, String orderNumber, String userName);
    
    /**
     * Validate email address format and deliverability
     * @param email Email address to validate
     * @return true if email is valid and deliverable, false otherwise
     */
    boolean isValidEmail(String email);
}
