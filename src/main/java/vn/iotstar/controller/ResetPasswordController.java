package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.RequestValidator;

@WebServlet("/reset-password")
public class ResetPasswordController extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final OtpService otps = new OtpServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { if (email(request) == null) { response.sendRedirect(request.getContextPath() + "/forgot-password"); return; } request.getRequestDispatcher("/views/reset-password.jsp").forward(request, response); }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = email(request);
        if (email == null) { response.sendRedirect(request.getContextPath() + "/forgot-password"); return; }
        try {
            String password = RequestValidator.password(request.getParameter("password"), "Mật khẩu mới");
            if (!password.equals(request.getParameter("confirmPassword"))) throw new IllegalArgumentException("Xác nhận mật khẩu không khớp.");
            String otp = RequestValidator.otp(request.getParameter("otp"));
            if (!otps.verify(email, "RESET_PASSWORD", otp)) { show(request, response, "OTP không đúng, đã hết hạn hoặc đã được sử dụng."); return; }
            users.resetPassword(email, password); request.getSession().removeAttribute("resetEmail"); response.sendRedirect(request.getContextPath() + "/login?reset=1");
        } catch (IllegalArgumentException exception) { show(request, response, exception.getMessage()); }
    }

    private String email(HttpServletRequest request) { Object value = request.getSession().getAttribute("resetEmail"); return value instanceof String email ? email : null; }
    private void show(HttpServletRequest request, HttpServletResponse response, String text) throws ServletException, IOException { request.setAttribute("alert", text); request.getRequestDispatcher("/views/reset-password.jsp").forward(request, response); }
}
