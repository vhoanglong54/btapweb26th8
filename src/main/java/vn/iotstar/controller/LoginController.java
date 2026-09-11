package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.RequestValidator;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    private final UserService users = new UserServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) { response.sendRedirect(request.getContextPath() + "/waiting"); return; }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) for (Cookie cookie : cookies) if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
            User account = users.get(cookie.getValue());
            if (account != null && account.isActive() && account.isEnabled()) { request.getSession(true).setAttribute(Constant.SESSION_ACCOUNT, account); response.sendRedirect(request.getContextPath() + "/waiting"); return; }
        }
        request.getRequestDispatcher(Constant.LOGIN).forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String username = RequestValidator.username(request.getParameter("username"));
            // Existing accounts may have a legacy password shorter than the new-password policy.
            String password = RequestValidator.required(request.getParameter("password"), "Mật khẩu", 128);
            User user = users.login(username, password);
            if (user == null) { show(request, response, "Tài khoản hoặc mật khẩu không đúng."); return; }
            if (!user.isEnabled()) { show(request, response, "Tài khoản đã bị quản trị viên tạm khóa."); return; }
            if (!user.isActive()) { request.getSession(true).setAttribute("activationEmail", user.getEmail()); response.sendRedirect(request.getContextPath() + "/activate"); return; }
            request.getSession(true).setAttribute(Constant.SESSION_ACCOUNT, user);
            if ("on".equals(request.getParameter("remember"))) {
                Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, username);
                cookie.setMaxAge(30 * 60); cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath()); response.addCookie(cookie);
            }
            response.sendRedirect(request.getContextPath() + "/waiting");
        } catch (IllegalArgumentException exception) { show(request, response, exception.getMessage()); }
    }

    private void show(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException { request.setAttribute("alert", message); request.getRequestDispatcher(Constant.LOGIN).forward(request, response); }
}
