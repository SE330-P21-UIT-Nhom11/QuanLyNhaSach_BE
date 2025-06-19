package com.example.quanlynhasach.service.impl;

import com.example.quanlynhasach.service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;    @Value("${MAIL_FROM}")
    private String fromEmail;

    @Value("${MAIL_FROM_NAME}")
    private String fromName;

    public EmailServiceImpl() {
        // Constructor
    }

    @PostConstruct
    public void init() {
        log.info("EmailService initialized with fromEmail: {}, fromName: {}", fromEmail, fromName);
    }

    // Email validation pattern
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Validate email format
     */
    private boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(email.trim()).matches();
    }

    /**
     * Check if email exists by attempting to send a test message
     * Note: This is a basic check and may not be 100% accurate
     */
    private boolean isEmailDeliverable(String email) {
        try {
            // Basic format check first
            if (!isValidEmailFormat(email)) {
                return false;
            }
            
            // Additional checks can be added here:
            // 1. DNS MX record validation
            // 2. SMTP connection test
            // For now, we'll assume valid format emails are deliverable
            
            return true;
        } catch (Exception e) {
            log.warn("Email deliverability check failed for: {}", email, e);
            return false;
        }
    }

    /**
     * Validate email before sending
     */
    private void validateEmailBeforeSending(String email) throws MessagingException {
        if (!isValidEmailFormat(email)) {
            throw new MessagingException("Invalid email format: " + email);
        }
        
        if (!isEmailDeliverable(email)) {
            throw new MessagingException("Email may not be deliverable: " + email);
        }
        
        log.debug("Email validation passed for: {}", email);
    }

    /**
     * Public method to check if email is valid
     * Can be used by other services before attempting to send emails
     */
    public boolean isValidEmail(String email) {
        try {
            return isValidEmailFormat(email) && isEmailDeliverable(email);
        } catch (Exception e) {
            log.warn("Email validation failed for: {}", email, e);
            return false;
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            // Validate email before sending
            validateEmailBeforeSending(to);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Email validation failed for: {}", to, e);
            throw e;
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new MessagingException("Failed to send HTML email", e);
        }
    }

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        try {
            String subject = "Đặt lại mật khẩu - QuanLyNhaSach";
            
            // Frontend reset password URL with token
            String resetPasswordUrl = frontendUrl + "/reset-password?token=" + resetToken;
            
            // Enhanced HTML content with link to frontend
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 24px;">QuanLyNhaSach</h1>
                        <p style="color: white; margin: 10px 0 0 0; opacity: 0.9;">Đặt lại mật khẩu của bạn</p>
                    </div>
                    
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin-bottom: 20px;">Yêu cầu đặt lại mật khẩu</h2>
                        <p style="color: #666; line-height: 1.6; margin-bottom: 20px;">
                            Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>%s</strong>. 
                            Nhấp vào nút bên dưới để đặt lại mật khẩu của bạn.
                        </p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; font-size: 16px;">
                                Đặt lại mật khẩu
                            </a>
                        </div>
                        
                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #007bff;">
                            <h4 style="color: #007bff; margin: 0 0 10px 0;">Mã reset token:</h4>
                            <p style="font-family: 'Courier New', monospace; background: white; padding: 10px; border-radius: 3px; margin: 0; word-break: break-all; font-size: 14px;">
                                %s
                            </p>
                            <p style="margin: 10px 0 0 0; font-size: 12px; color: #666;">
                                Bạn có thể sao chép mã này nếu link không hoạt động
                            </p>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                <strong>⚠️ Lưu ý quan trọng:</strong><br>
                                • Link này chỉ có hiệu lực trong <strong>5 phút</strong><br>
                                • Chỉ sử dụng được một lần<br>
                                • Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này
                            </p>
                        </div>
                        
                        <p style="color: #999; font-size: 12px; margin-top: 30px; text-align: center;">
                            Nếu nút không hoạt động, bạn có thể sao chép và dán link sau vào trình duyệt:<br>
                            <span style="word-break: break-all;">%s</span>
                        </p>
                    </div>
                    
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e0e0e0;">
                        <p style="color: #999; font-size: 12px; margin: 0;">
                            Email này được gửi tự động từ QuanLyNhaSach. Vui lòng không trả lời email này.<br>
                            © 2025 QuanLyNhaSach. Tất cả quyền được bảo lưu.
                        </p>
                    </div>
                </div>
                """, to, resetPasswordUrl, resetToken, resetPasswordUrl);

            sendHtmlEmail(to, subject, htmlContent);
            log.info("Password reset email sent successfully to: {} with token: {}", to, resetToken);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            // Don't throw exception for password reset emails to avoid security issues
        }
    }    @Override
    public void sendWelcomeEmail(String to, String userName, String userEmail, String temporaryPassword) {
        try {
            // Validate email before sending
            validateEmailBeforeSending(to);
            
            String subject = "Chào mừng đến với BookManager - Thông tin tài khoản của bạn";
            
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #ffffff; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #27ae60 0%%, #2ecc71 100%%); padding: 30px; text-align: center;">
                        <h1 style="color: white; margin: 0; font-size: 24px;">BookManager</h1>
                        <p style="color: white; margin: 10px 0 0 0; opacity: 0.9;">Chào mừng bạn đến với hệ thống!</p>
                    </div>
                    
                    <div style="padding: 30px;">
                        <h2 style="color: #333; margin-bottom: 20px;">Chào mừng bạn đến với BookManager!</h2>
                        <p style="color: #666; line-height: 1.6; margin-bottom: 20px;">
                            Xin chào <strong>%s</strong>,
                        </p>
                        <p style="color: #666; line-height: 1.6; margin-bottom: 20px;">
                            Tài khoản của bạn đã được tạo thành công tại BookManager.
                            Dưới đây là thông tin đăng nhập của bạn:
                        </p>
                        
                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #27ae60;">
                            <h4 style="color: #27ae60; margin: 0 0 15px 0;">🔐 Thông tin đăng nhập:</h4>
                            <div style="margin-bottom: 10px;">
                                <strong style="color: #333;">Email/Tên đăng nhập:</strong><br>
                                <span style="font-family: 'Courier New', monospace; background: white; padding: 5px 10px; border-radius: 3px; color: #2c3e50; font-size: 14px; display: inline-block; margin-top: 5px;">%s</span>
                            </div>
                            <div style="margin-top: 15px;">
                                <strong style="color: #333;">Mật khẩu tạm thời:</strong><br>
                                <span style="font-family: 'Courier New', monospace; background: white; padding: 5px 10px; border-radius: 3px; color: #e74c3c; font-size: 14px; display: inline-block; margin-top: 5px; font-weight: bold;">%s</span>
                            </div>
                        </div>
                        
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404; font-size: 14px;">
                                <strong>🔒 Lưu ý bảo mật quan trọng:</strong><br>
                                • Vui lòng đổi mật khẩu ngay sau lần đăng nhập đầu tiên<br>
                                • Không chia sẻ thông tin đăng nhập với ai khác<br>
                                • Sử dụng mật khẩu mạnh cho tài khoản của bạn<br>
                                • Đăng xuất khỏi tài khoản khi sử dụng máy tính chung
                            </p>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <p style="color: #2c3e50; font-size: 18px; margin-bottom: 15px;">
                                🎉 Chúc bạn có trải nghiệm tuyệt vời! 🎉
                            </p>
                            <p style="color: #666; line-height: 1.6;">
                                Bạn có thể bắt đầu khám phá và quản lý thư viện ngay bây giờ!
                            </p>
                        </div>
                    </div>
                    
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #e0e0e0;">
                        <p style="color: #999; font-size: 12px; margin: 0;">
                            Email này được gửi tự động từ BookManager. Vui lòng không trả lời email này.<br>
                            Nếu có thắc mắc, vui lòng liên hệ với quản trị viên hệ thống.<br>
                            © 2025 BookManager. Tất cả quyền được bảo lưu.
                        </p>
                    </div>
                </div>
                """, userName, userEmail, temporaryPassword);            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to: {}", to, e);
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
