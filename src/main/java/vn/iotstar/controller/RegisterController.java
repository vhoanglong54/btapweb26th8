package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.service.OtpService;
import vn.iotstar.service.impl.OtpServiceImpl;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private final UserService service=new UserServiceImpl();
    private final OtpService otpService=new OtpServiceImpl();
    protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {if(req.getSession(false)!=null&&req.getSession(false).getAttribute(Constant.SESSION_ACCOUNT)!=null){resp.sendRedirect(req.getContextPath()+"/waiting");return;}req.getRequestDispatcher(Constant.REGISTER).forward(req,resp);}
    protected void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {
        req.setCharacterEncoding("UTF-8");String username=trim(req.getParameter("username")),password=req.getParameter("password"),email=trim(req.getParameter("email")),fullname=trim(req.getParameter("fullname")),phone=trim(req.getParameter("phone"));
        if(blank(username)||blank(password)||blank(email)||blank(fullname)){show(req,resp,"Vui lòng nhập đủ các trường bắt buộc");return;}
        if(password.length()<8){show(req,resp,"Mật khẩu phải có ít nhất 8 ký tự");return;}
        if(service.checkExistEmail(email)){show(req,resp,"Email đã tồn tại!");return;} if(service.checkExistUsername(username)){show(req,resp,"Tài khoản đã tồn tại!");return;} if(!blank(phone)&&service.checkExistPhone(phone)){show(req,resp,"Số điện thoại đã tồn tại!");return;}
        try { if(service.register(username,password,email,fullname,phone)){req.getSession().setAttribute("activationEmail",email);otpService.send(email,"ACTIVATE");resp.sendRedirect(req.getContextPath()+"/activate");}else show(req,resp,"System error!"); }
        catch (RuntimeException e) { req.setAttribute("alert","Tạo tài khoản thành công nhưng không gửi được OTP: "+e.getMessage()); req.getRequestDispatcher("/views/activate.jsp").forward(req,resp); }
    }
    private boolean blank(String v){return v==null||v.trim().isEmpty();} private String trim(String v){return v==null?null:v.trim();} private void show(HttpServletRequest req,HttpServletResponse resp,String m)throws ServletException,IOException{req.setAttribute("alert",m);req.getRequestDispatcher(Constant.REGISTER).forward(req,resp);}
}
