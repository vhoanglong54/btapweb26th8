package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.model.User;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.RequestValidator;

@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final OtpService otps = new OtpServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response); }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String email = RequestValidator.email(request.getParameter("email"));
            User user = users.getByEmail(email);
            if (user != null && user.isEnabled()) { otps.send(email, "RESET_PASSWORD"); request.getSession().setAttribute("resetEmail", email); response.sendRedirect(request.getContextPath() + "/reset-password"); return; }
            show(request, response, "Nếu email tồn tại, mã OTP đã được gửi.");
        } catch (IllegalArgumentException exception) { show(request, response, exception.getMessage()); }
        catch (RuntimeException exception) { show(request, response, "Không thể gửi OTP. Vui lòng thử lại sau."); }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String text) throws ServletException, IOException { request.setAttribute("alert", text); request.getRequestDispatcher("/views/forgot-password.jsp").forward(request, response); }
}
