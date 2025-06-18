package com.example.quanlynhasach.service;

import jakarta.mail.MessagingException;

/**
 * Service interface for email operations
 */
public interface EmailService {
    
    /**
     * Send a simple text email
     * @param to Recipient email address
     * @param subject Email subject
     * @param text Email content
     */
    void sendSimpleEmail(String to, String subject, String text);
    
    /**
     * Send HTML email
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content
     * @throws MessagingException if email sending fails
     */
    void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException;
    
    /**
     * Send email using template
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Template name (without .html extension)
     * @param variables Template variables
     * @throws MessagingException if email sending fails
     */
    void sendTemplateEmail(String to, String subject, String templateName, Object variables) throws MessagingException;
    
    /**
     * Send password reset email
     * @param to Recipient email address
     * @param resetToken Reset token
     * @param userName User name
     */
    void sendPasswordResetEmail(String to, String resetToken, String userName);
    
    /**
     * Send welcome email to new user
     * @param to Recipient email address
     * @param userName User name
     */
    void sendWelcomeEmail(String to, String userName);
    
    /**
     * Send order confirmation email
     * @param to Recipient email address
     * @param orderNumber Order number
     * @param userName User name
     */
    void sendOrderConfirmationEmail(String to, String orderNumber, String userName);
}
