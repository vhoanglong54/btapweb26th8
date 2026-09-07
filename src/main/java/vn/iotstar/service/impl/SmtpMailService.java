package vn.iotstar.service.impl;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.iotstar.dao.SmtpSettingsDao;
import vn.iotstar.dao.impl.SmtpSettingsDaoImpl;
import vn.iotstar.model.SmtpSettings;
import vn.iotstar.service.MailService;

/** Uses admin-managed SMTP settings first, then environment settings as a deployment fallback. */
public class SmtpMailService implements MailService {
    private final SmtpSettingsDao settingsDao = new SmtpSettingsDaoImpl();

    @Override public void sendOtp(String email, String purpose, String code) {
        send(email, purpose.equals("ACTIVATE") ? "Kích hoạt tài khoản" : "Đặt lại mật khẩu", "Mã OTP của bạn là: " + code + "\nMã có hiệu lực trong 10 phút và chỉ dùng một lần.");
    }

    public void sendTestEmail(String email) {
        send(email, "Kiểm tra cấu hình SMTP", "Gửi thành công. Dragon Store đã sẵn sàng gửi email OTP.");
    }

    private void send(String recipient, String subject, String body) {
        SmtpSettings smtp = loadSettings();
        if (!smtp.isComplete()) throw new IllegalStateException("Chưa cấu hình SMTP. Đăng nhập admin và mở Cấu hình email OTP.");
        Properties properties = new Properties(); properties.put("mail.smtp.host", smtp.getHost()); properties.put("mail.smtp.port", String.valueOf(smtp.getPort())); properties.put("mail.smtp.auth", "true"); properties.put("mail.smtp.starttls.enable", String.valueOf(smtp.isStartTls()));
        Session session = Session.getInstance(properties, new Authenticator() { @Override protected PasswordAuthentication getPasswordAuthentication() { return new PasswordAuthentication(smtp.getUsername(), smtp.getPassword()); } });
        try { MimeMessage message = new MimeMessage(session); message.setFrom(new InternetAddress(smtp.getFromEmail())); message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient)); message.setSubject(subject, "UTF-8"); message.setText(body, "UTF-8"); Transport.send(message); }
        catch (MessagingException e) { throw new IllegalStateException("Không thể gửi email OTP", e); }
    }

    private SmtpSettings loadSettings() {
        SmtpSettings stored = settingsDao.find();
        if (stored != null && stored.isComplete()) return stored;
        SmtpSettings fallback = new SmtpSettings();
        fallback.setHost(setting("MAIL_HOST")); fallback.setUsername(setting("MAIL_USERNAME")); fallback.setPassword(setting("MAIL_PASSWORD")); fallback.setFromEmail(setting("MAIL_FROM"));
        fallback.setPort(parsePort(value("MAIL_PORT", "587"))); fallback.setStartTls(Boolean.parseBoolean(value("MAIL_STARTTLS", "true")));
        return fallback;
    }

    private int parsePort(String port) { try { return Integer.parseInt(port); } catch (NumberFormatException exception) { return 0; } }
    private String setting(String name) { String value = System.getProperty(name); return blank(value) ? System.getenv(name) : value; }
    private String value(String name, String fallback) { String value = setting(name); return blank(value) ? fallback : value; }
    private boolean blank(String value) { return value == null || value.isBlank(); }
}
