package vn.iotstar.controller;

import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Product;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.ProductServiceImpl;

@WebServlet({"/admin/products", "/admin/product/add", "/admin/product/edit", "/admin/product/delete"})
public class ProductAdminController extends HttpServlet {
    private final ProductService products = new ProductServiceImpl(); private final CategoryService categories = new CategoryServiceImpl();
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if (path.endsWith("/add")) { form(req, resp, null); return; }
            if (path.endsWith("/edit")) { Product product = products.findById(id(req)); if (product == null) { resp.sendError(404); return; } form(req, resp, product); return; }
            if (path.endsWith("/delete")) { resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
            req.setAttribute("products", products.findAllForAdmin()); req.getRequestDispatcher("/views/admin/list-product.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) { resp.sendError(400, e.getMessage()); }
    }
    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); boolean edit = "true".equals(req.getParameter("edit"));
        try { if (req.getServletPath().endsWith("/delete")) { products.delete(id(req)); resp.sendRedirect(req.getContextPath() + "/admin/products"); return; } Product product = bind(req); if (edit) { product.setProductId(id(req)); Product old = products.findById(product.getProductId()); if (old == null) { resp.sendError(404); return; } product.setCreatedAt(old.getCreatedAt()); products.update(product, categoryId(req)); } else products.create(product, categoryId(req)); resp.sendRedirect(req.getContextPath() + "/admin/products"); }
        catch (IllegalArgumentException e) { req.setAttribute("alert", e.getMessage()); form(req, resp, null); }
    }
    private Product bind(HttpServletRequest req) { Product p = new Product(); p.setName(trim(req.getParameter("name"))); p.setDescription(trim(req.getParameter("description"))); p.setImage(trim(req.getParameter("image"))); p.setPrice(new BigDecimal(req.getParameter("price"))); p.setStatus("0".equals(req.getParameter("status")) ? 0 : 1); return p; }
    private void form(HttpServletRequest req, HttpServletResponse resp, Product product) throws ServletException, IOException { req.setAttribute("product", product); req.setAttribute("categories", categories.findAll()); req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp); }
    private int id(HttpServletRequest req) { return Integer.parseInt(req.getParameter("id")); } private int categoryId(HttpServletRequest req) { return Integer.parseInt(req.getParameter("categoryId")); } private String trim(String value) { return value == null ? null : value.trim(); }
}
