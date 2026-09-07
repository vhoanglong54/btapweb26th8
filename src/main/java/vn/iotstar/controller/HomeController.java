package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

/** Public home page: exactly the ten newest visible products. */
@WebServlet("/home")
public class HomeController extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("products", products.newest(10));
        request.getRequestDispatcher("/views/home.jsp").forward(request, response);
    }
}
