package vn.iotstar.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import vn.iotstar.model.User;
import vn.iotstar.util.Constant;

@WebFilter("/admin/*")
public class AdminAuthorizationFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request; HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); Object account = session == null ? null : session.getAttribute(Constant.SESSION_ACCOUNT);
        if (!(account instanceof User user)) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if (user.getRoleid() != 1) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
        chain.doFilter(request, response);
    }
}
