package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.dao.SmtpSettingsDao;
import vn.iotstar.dao.impl.SmtpSettingsDaoImpl;
import vn.iotstar.model.SmtpSettings;
import vn.iotstar.service.impl.SmtpMailService;
import vn.iotstar.util.RequestValidator;

/** Admin-only screen for SMTP settings. The password is write-only in the UI. */
@WebServlet("/admin/mail-settings")
public class SmtpSettingsController extends HttpServlet {
    private static final String ADMIN_EMAIL = "vhoanglong54@gmail.com";
    private static final String GMAIL_HOST = "smtp.gmail.com";
    private static final int GMAIL_PORT = 587;
    private final SmtpSettingsDao settingsDao = new SmtpSettingsDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        show(request, response, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        SmtpSettings stored = settingsDao.find();
        SmtpSettings settings = bind(request);
        boolean replacePassword = !blank(settings.getPassword());
        if (!replacePassword && stored != null) settings.setPassword(stored.getPassword());
        String error = validate(settings);
        if (error != null) { show(request, response, settings, error); return; }
        try {
            settingsDao.save(settings, replacePassword);
            if ("test".equals(request.getParameter("action"))) {
                String testEmail = RequestValidator.email(request.getParameter("testEmail"));
                new SmtpMailService().sendTestEmail(testEmail);
                show(request, response, settings, null, "Đã lưu cấu hình và gửi email thử tới " + testEmail + ".");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/mail-settings?saved=1");
        } catch (IllegalStateException exception) {
            show(request, response, settings, readableMessage(exception));
        }
    }

    private SmtpSettings bind(HttpServletRequest request) {
        SmtpSettings settings = new SmtpSettings();
        settings.setHost(GMAIL_HOST);
        settings.setUsername(ADMIN_EMAIL);
        settings.setPassword(normalizeAppPassword(request.getParameter("password")));
        settings.setFromEmail(ADMIN_EMAIL);
        settings.setStartTls(true);
        settings.setPort(GMAIL_PORT);
        return settings;
    }

    private String validate(SmtpSettings settings) {
        if (blank(settings.getPassword())) return "Hãy nhập Gmail App Password của vhoanglong54@gmail.com.";
        if (!settings.getPassword().matches("[A-Za-z0-9]{16}")) return "Gmail App Password phải gồm đúng 16 chữ và số.";
        return null;
    }

    private void show(HttpServletRequest request, HttpServletResponse response, SmtpSettings input, String error) throws ServletException, IOException {
        show(request, response, input, error, null);
    }

    private void show(HttpServletRequest request, HttpServletResponse response, SmtpSettings input, String error, String success) throws ServletException, IOException {
        SmtpSettings settings = input == null ? settingsDao.find() : input;
        boolean passwordSaved = settings != null && !blank(settings.getPassword());
        if (settings == null) {
            settings = new SmtpSettings();
            settings.setHost(GMAIL_HOST);
            settings.setPort(GMAIL_PORT);
            settings.setUsername(ADMIN_EMAIL);
            settings.setFromEmail(ADMIN_EMAIL);
            settings.setStartTls(true);
        }
        settings.setPassword(null);
        request.setAttribute("settings", settings);
        request.setAttribute("passwordSaved", passwordSaved);
        request.setAttribute("alert", error);
        request.setAttribute("success", success);
        request.getRequestDispatcher("/views/admin/mail-settings.jsp").forward(request, response);
    }

    private String readableMessage(IllegalStateException exception) {
        return exception.getCause() == null ? exception.getMessage() : "Không thể kết nối SMTP. Kiểm tra lại máy chủ, cổng và App Password.";
    }
    private String trim(String value) { return value == null ? null : value.trim(); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private String normalizeAppPassword(String value) { String password = trim(value); return password == null ? null : password.replace(" ", ""); }
}
