package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.util.Constant;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException { logout(req, resp); }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException { logout(req, resp); }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        Cookie rememberCookie = new Cookie(Constant.COOKIE_REMEMBER, "");
        rememberCookie.setMaxAge(0);
        rememberCookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        resp.addCookie(rememberCookie);
        resp.sendRedirect(req.getContextPath() + "/login?logout=1");
    }
}
