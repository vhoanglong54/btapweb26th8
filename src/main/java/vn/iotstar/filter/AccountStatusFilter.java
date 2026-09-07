package vn.iotstar.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

/** Refreshes a signed-in account so an admin lock takes effect on the next request. */
@WebFilter("/*")
public class AccountStatusFilter implements Filter {
    private final UserService users = new UserServiceImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        Object account = session == null ? null : session.getAttribute(Constant.SESSION_ACCOUNT);
        if (account instanceof User sessionUser) {
            User currentUser = users.get(sessionUser.getUserName());
            if (currentUser == null || !currentUser.isEnabled()) {
                session.invalidate();
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?disabled=1");
                return;
            }
            session.setAttribute(Constant.SESSION_ACCOUNT, currentUser);
        }
        chain.doFilter(request, response);
    }
}
