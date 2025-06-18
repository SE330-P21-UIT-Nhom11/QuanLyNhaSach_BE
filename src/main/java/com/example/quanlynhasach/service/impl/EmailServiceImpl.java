package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${MAIL_FROM}")
    private String fromEmail;

    @Value("${MAIL_FROM_NAME}")
    private String fromName;

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new MessagingException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendTemplateEmail(String to, String subject, String templateName, Object variables) throws MessagingException {
        try {
            Context context = new Context();
            
            // Add variables to template context
            if (variables instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> variableMap = (Map<String, Object>) variables;
                variableMap.forEach(context::setVariable);
            }

            String htmlContent = templateEngine.process(templateName, context);
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Template email sent successfully to: {} using template: {}", to, templateName);
        } catch (Exception e) {
            log.error("Failed to send template email to: {} using template: {}", to, templateName, e);
            throw new MessagingException("Failed to send template email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken, String userName) {
        try {
            String subject = "Đặt lại mật khẩu - QuanLyNhaSach";
            
            // Simple HTML content for password reset
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #2c3e50;">Đặt lại mật khẩu</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>
                    <p>Mã đặt lại mật khẩu của bạn là:</p>
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0;">
                        <h3 style="color: #e74c3c; font-size: 24px; margin: 0;">%s</h3>
                    </div>
                    <p><strong>Lưu ý:</strong> Mã này chỉ có hiệu lực trong 5 phút.</p>
                    <p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>
                    <hr style="margin: 30px 0;">
                    <p style="color: #7f8c8d; font-size: 12px;">
                        Email này được gửi tự động, vui lòng không trả lời.
                    </p>
                </div>
                """, userName, resetToken);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            // Don't throw exception for password reset emails to avoid security issues
        }
    }

    @Override
    public void sendWelcomeEmail(String to, String userName) {
        try {
            String subject = "Chào mừng đến với QuanLyNhaSach!";
            
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #27ae60;">Chào mừng bạn đến với QuanLyNhaSach!</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Cảm ơn bạn đã đăng ký tài khoản tại QuanLyNhaSach.</p>
                    <p>Bạn có thể bắt đầu khám phá và mua sắm ngay bây giờ!</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <p style="color: #2c3e50; font-size: 18px;">
                            🎉 Chúc bạn có trải nghiệm tuyệt vời! 🎉
                        </p>
                    </div>
                    <hr style="margin: 30px 0;">
                    <p style="color: #7f8c8d; font-size: 12px;">
                        Email này được gửi tự động, vui lòng không trả lời.
                    </p>
                </div>
                """, userName);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }

    @Override
    public void sendOrderConfirmationEmail(String to, String orderNumber, String userName) {
        try {
            String subject = "Xác nhận đơn hàng #" + orderNumber;
            
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #3498db;">Xác nhận đơn hàng</h2>
                    <p>Xin chào <strong>%s</strong>,</p>
                    <p>Cảm ơn bạn đã đặt hàng tại QuanLyNhaSach!</p>
                    <div style="background-color: #e8f6fd; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>Mã đơn hàng:</strong> #%s</p>
                        <p><strong>Trạng thái:</strong> Đang xử lý</p>
                    </div>
                    <p>Chúng tôi sẽ tiến hành xử lý đơn hàng và thông báo cho bạn sớm nhất.</p>
                    <p>Bạn có thể theo dõi trạng thái đơn hàng trong tài khoản của mình.</p>
                    <hr style="margin: 30px 0;">
                    <p style="color: #7f8c8d; font-size: 12px;">
                        Email này được gửi tự động, vui lòng không trả lời.
                    </p>
                </div>
                """, userName, orderNumber);

            sendHtmlEmail(to, subject, htmlContent);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}", to, e);
        }
    }
}
