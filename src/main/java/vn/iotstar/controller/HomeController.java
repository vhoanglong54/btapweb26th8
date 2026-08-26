package vn.iotstar.controller;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
@WebServlet("/home") public class HomeController extends HttpServlet { protected void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException {req.getRequestDispatcher("/views/home.jsp").forward(req,resp);} }
