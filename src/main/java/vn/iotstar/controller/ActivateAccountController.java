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

@WebServlet("/activate")
public class ActivateAccountController extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final OtpService otps = new OtpServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { if (email(request) == null) { response.sendRedirect(request.getContextPath() + "/login"); return; } request.getRequestDispatcher("/views/activate.jsp").forward(request, response); }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = email(request);
        if (email == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        try {
            User user = users.getByEmail(email);
            if (user == null || !user.isEnabled()) { message(request, response, "Tài khoản đã bị quản trị viên khóa."); return; }
            if ("resend".equals(request.getParameter("action"))) { otps.send(email, "ACTIVATE"); message(request, response, "Đã gửi OTP mới đến email của bạn."); return; }
            String otp = RequestValidator.otp(request.getParameter("otp"));
            if (!otps.verify(email, "ACTIVATE", otp)) { message(request, response, "OTP không đúng, đã hết hạn hoặc đã được sử dụng."); return; }
            users.activate(email); request.getSession().removeAttribute("activationEmail"); response.sendRedirect(request.getContextPath() + "/login?activated=1");
        } catch (IllegalArgumentException exception) { message(request, response, exception.getMessage()); }
        catch (RuntimeException exception) { message(request, response, "Không thể gửi hoặc xác thực OTP. Vui lòng thử lại sau."); }
    }

    private String email(HttpServletRequest request) { Object value = request.getSession().getAttribute("activationEmail"); return value instanceof String email ? email : null; }
    private void message(HttpServletRequest request, HttpServletResponse response, String text) throws ServletException, IOException { request.setAttribute("alert", text); request.getRequestDispatcher("/views/activate.jsp").forward(request, response); }
}
