package vn.iotstar.controller;
import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
@WebServlet("/logout") public class LogoutController extends HttpServlet { protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws IOException {HttpSession s=req.getSession(false);if(s!=null)s.invalidate();resp.sendRedirect(req.getContextPath()+"/login");} }
