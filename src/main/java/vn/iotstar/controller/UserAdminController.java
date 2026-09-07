package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.OtpServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

/** Admin account management: visibility, enable/disable and activation resend. */
@WebServlet("/admin/users")
public class UserAdminController extends HttpServlet {
    private final UserService users = new UserServiceImpl();
    private final OtpService otps = new OtpServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        show(request, response, null);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int userId = integer(request.getParameter("id"));
        User target = users.getById(userId);
        if (target == null) { show(request, response, "Không tìm thấy người dùng."); return; }
        User current = currentUser(request.getSession(false));
        if (current != null && current.getId() == target.getId()) { show(request, response, "Không thể khóa hoặc mở khóa chính tài khoản đang đăng nhập."); return; }
        try {
            if ("toggle-enabled".equals(request.getParameter("action"))) {
                users.setEnabled(target.getId(), !target.isEnabled());
                response.sendRedirect(request.getContextPath() + "/admin/users?updated=1");
                return;
            }
            if ("resend-activation".equals(request.getParameter("action"))) {
                if (target.isActive()) { show(request, response, "Tài khoản này đã kích hoạt, không cần gửi lại OTP."); return; }
                if (!target.isEnabled()) { show(request, response, "Hãy mở khóa tài khoản trước khi gửi lại OTP."); return; }
                otps.send(target.getEmail(), "ACTIVATE");
                show(request, response, "Đã gửi lại OTP kích hoạt tới " + target.getEmail() + ".");
                return;
            }
            show(request, response, "Thao tác không hợp lệ.");
        } catch (RuntimeException exception) {
            show(request, response, "Không thể hoàn tất thao tác: " + readableMessage(exception));
        }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException {
        request.setAttribute("users", users.findAll());
        request.setAttribute("alert", message);
        request.getRequestDispatcher("/views/admin/list-user.jsp").forward(request, response);
    }
    private User currentUser(HttpSession session) { return session == null ? null : (User) session.getAttribute(Constant.SESSION_ACCOUNT); }
    private int integer(String value) { try { return Integer.parseInt(value); } catch (Exception exception) { return 0; } }
    private String readableMessage(RuntimeException exception) { return exception.getCause() == null ? exception.getMessage() : "kiểm tra cấu hình SMTP hoặc kết nối database."; }
}
