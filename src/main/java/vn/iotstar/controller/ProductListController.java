package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

@WebServlet("/product")
public class ProductListController extends HttpServlet {
    private static final int PAGE_SIZE = 6;
    private final ProductService products = new ProductServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = page(req.getParameter("page"));
        long total = products.count();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        if (page > totalPages) page = totalPages;
        req.setAttribute("products", products.findPage(page, PAGE_SIZE));
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/views/products.jsp").forward(req, resp);
    }
    private int page(String value) { try { int page = Integer.parseInt(value); return page > 0 ? page : 1; } catch (Exception e) { return 1; } }
}
