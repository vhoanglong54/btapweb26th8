package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Product;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.ProductServiceImpl;

@WebServlet("/product/detail")
public class ProductDetailController extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { try { Product product = products.findById(Integer.parseInt(req.getParameter("id"))); if (product == null || product.getStatus() == 0) { resp.sendError(404); return; } req.setAttribute("product", product); req.getRequestDispatcher("/views/product-detail.jsp").forward(req, resp); } catch (NumberFormatException e) { resp.sendError(400, "ID sản phẩm không hợp lệ"); } }
}
