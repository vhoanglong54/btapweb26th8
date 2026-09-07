package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private final UserService users = new UserServiceImpl(); private final OtpService otps = new OtpServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email"); if (email == null || email.isBlank()) { show(req, resp, "Vui lòng nhập email."); return; }
        vn.iotstar.model.User user = users.getByEmail(email.trim());
        if (user != null && user.isEnabled()) { try { otps.send(email.trim(), "RESET_PASSWORD"); req.getSession().setAttribute("resetEmail", email.trim()); resp.sendRedirect(req.getContextPath() + "/reset-password"); return; } catch (RuntimeException e) { show(req, resp, "Không thể gửi OTP: " + e.getMessage()); return; } }
        show(req, resp, "Nếu email tồn tại, mã OTP đã được gửi.");
    }
    private void show(HttpServletRequest req, HttpServletResponse resp, String text) throws ServletException, IOException { req.setAttribute("alert", text); req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp); }
}
