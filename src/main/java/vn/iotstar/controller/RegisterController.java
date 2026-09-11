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
import vn.iotstar.util.Constant;
import vn.iotstar.util.RequestValidator;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final OtpService otps = new OtpServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) != null && request.getSession(false).getAttribute(Constant.SESSION_ACCOUNT) != null) { response.sendRedirect(request.getContextPath() + "/waiting"); return; }
        request.getRequestDispatcher(Constant.REGISTER).forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String username = RequestValidator.username(request.getParameter("username"));
            String password = RequestValidator.password(request.getParameter("password"), "Mật khẩu");
            String email = RequestValidator.email(request.getParameter("email"));
            String fullName = RequestValidator.required(request.getParameter("fullname"), "Họ và tên", 100);
            if (fullName.length() < 2) throw new IllegalArgumentException("Họ và tên phải có ít nhất 2 ký tự.");
            String phone = RequestValidator.phone(request.getParameter("phone"));
            if (users.checkExistEmail(email)) throw new IllegalArgumentException("Email đã tồn tại.");
            if (users.checkExistUsername(username)) throw new IllegalArgumentException("Tài khoản đã tồn tại.");
            if (phone != null && users.checkExistPhone(phone)) throw new IllegalArgumentException("Số điện thoại đã tồn tại.");
            if (!users.register(username, password, email, fullName, phone)) throw new IllegalArgumentException("Không thể tạo tài khoản. Vui lòng thử lại.");
            request.getSession().setAttribute("activationEmail", email);
            otps.send(email, "ACTIVATE");
            response.sendRedirect(request.getContextPath() + "/activate");
        } catch (IllegalArgumentException exception) { show(request, response, exception.getMessage()); }
        catch (RuntimeException exception) { request.getSession().setAttribute("activationEmail", RequestValidator.trim(request.getParameter("email"))); request.setAttribute("alert", "Tạo tài khoản thành công nhưng chưa gửi được OTP. Hãy cấu hình SMTP rồi gửi lại mã."); request.getRequestDispatcher("/views/activate.jsp").forward(request, response); }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException { request.setAttribute("alert", message); request.getRequestDispatcher(Constant.REGISTER).forward(request, response); }
}
