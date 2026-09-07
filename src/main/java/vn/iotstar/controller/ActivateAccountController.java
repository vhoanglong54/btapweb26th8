package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;

@WebServlet("/activate")
public class ActivateAccountController extends HttpServlet {
    private final UserService users = new UserServiceImpl(); private final OtpService otps = new OtpServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { if (email(req) == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; } req.getRequestDispatcher("/views/activate.jsp").forward(req, resp); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = email(req); if (email == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        try {
            vn.iotstar.model.User user = users.getByEmail(email);
            if (user == null || !user.isEnabled()) { message(req, resp, "Tài khoản đã bị quản trị viên khóa."); return; }
            if ("resend".equals(req.getParameter("action"))) { otps.send(email, "ACTIVATE"); message(req, resp, "Đã gửi OTP mới đến email của bạn."); return; }
            if (!otps.verify(email, "ACTIVATE", req.getParameter("otp"))) { message(req, resp, "OTP không đúng, đã hết hạn hoặc đã được sử dụng."); return; }
            users.activate(email); req.getSession().removeAttribute("activationEmail"); resp.sendRedirect(req.getContextPath() + "/login?activated=1");
        } catch (RuntimeException e) { message(req, resp, "Không thể gửi/xác thực OTP: " + e.getMessage()); }
    }
    private String email(HttpServletRequest req) { Object value = req.getSession().getAttribute("activationEmail"); return value instanceof String email ? email : null; }
    private void message(HttpServletRequest req, HttpServletResponse resp, String text) throws ServletException, IOException { req.setAttribute("alert", text); req.getRequestDispatcher("/views/activate.jsp").forward(req, resp); }
}
