package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private final UserService service=new UserServiceImpl();
    protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {if(req.getSession(false)!=null&&req.getSession(false).getAttribute(Constant.SESSION_ACCOUNT)!=null){resp.sendRedirect(req.getContextPath()+"/waiting");return;}req.getRequestDispatcher(Constant.REGISTER).forward(req,resp);}
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");String username=req.getParameter("username"),password=req.getParameter("password"),email=req.getParameter("email"),fullname=req.getParameter("fullname"),phone=req.getParameter("phone");
        if(blank(username)||blank(password)||blank(email)||blank(fullname)){show(req,resp,"Vui lòng nhập đủ các trường bắt buộc");return;}
        if(service.checkExistEmail(email)){show(req,resp,"Email đã tồn tại!");return;} if(service.checkExistUsername(username)){show(req,resp,"Tài khoản đã tồn tại!");return;} if(!blank(phone)&&service.checkExistPhone(phone)){show(req,resp,"Số điện thoại đã tồn tại!");return;}
        if(service.register(username,password,email,fullname,phone)){resp.sendRedirect(req.getContextPath()+"/login?registered=1");}else show(req,resp,"System error!");
    }
    private boolean blank(String v){return v==null||v.trim().isEmpty();} private void show(HttpServletRequest req,HttpServletResponse resp,String m)throws ServletException,IOException{req.setAttribute("alert",m);req.getRequestDispatcher(Constant.REGISTER).forward(req,resp);}
}
