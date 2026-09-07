package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;

@WebServlet("/reset-password")
public class ResetPasswordController extends HttpServlet {
    private final UserService users = new UserServiceImpl(); private final OtpService otps = new OtpServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { if (email(req) == null) { resp.sendRedirect(req.getContextPath() + "/forgot-password"); return; } req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = email(req), password = req.getParameter("password"), confirm = req.getParameter("confirmPassword");
        if (email == null) { resp.sendRedirect(req.getContextPath() + "/forgot-password"); return; }
        if (password == null || password.length() < 8) { show(req, resp, "Mật khẩu phải có ít nhất 8 ký tự."); return; }
        if (!password.equals(confirm)) { show(req, resp, "Xác nhận mật khẩu không khớp."); return; }
        if (!otps.verify(email, "RESET_PASSWORD", req.getParameter("otp"))) { show(req, resp, "OTP không đúng, đã hết hạn hoặc đã được sử dụng."); return; }
        users.resetPassword(email, password); req.getSession().removeAttribute("resetEmail"); resp.sendRedirect(req.getContextPath() + "/login?reset=1");
    }
    private String email(HttpServletRequest req) { Object value = req.getSession().getAttribute("resetEmail"); return value instanceof String email ? email : null; }
    private void show(HttpServletRequest req, HttpServletResponse resp, String text) throws ServletException, IOException { req.setAttribute("alert", text); req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp); }
}
