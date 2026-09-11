package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.Product;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.ProductServiceImpl;
import vn.iotstar.util.RequestValidator;

@WebServlet({"/admin/products", "/admin/product/add", "/admin/product/edit", "/admin/product/delete"})
public class ProductAdminController extends HttpServlet {
    private final ProductService products = new ProductServiceImpl();
    private final CategoryService categories = new CategoryServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            if (path.endsWith("/add")) { form(request, response, null, null); return; }
            if (path.endsWith("/edit")) { Product product = products.findById(RequestValidator.positiveId(request.getParameter("id"), "ID sản phẩm")); if (product == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; } form(request, response, product, null); return; }
            if (path.endsWith("/delete")) { response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED); return; }
            request.setAttribute("products", products.findAllForAdmin()); request.getRequestDispatcher("/views/admin/list-product.jsp").forward(request, response);
        } catch (IllegalArgumentException exception) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage()); }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            String path = request.getServletPath();
            if (path.endsWith("/delete")) { products.delete(RequestValidator.positiveId(request.getParameter("id"), "ID sản phẩm")); response.sendRedirect(request.getContextPath() + "/admin/products"); return; }
            Product product = bind(request);
            int categoryId = RequestValidator.positiveId(request.getParameter("categoryId"), "Danh mục");
            if (path.endsWith("/edit")) {
                product.setProductId(RequestValidator.positiveId(request.getParameter("id"), "ID sản phẩm"));
                Product old = products.findById(product.getProductId());
                if (old == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                product.setCreatedAt(old.getCreatedAt()); products.update(product, categoryId);
            } else if (path.endsWith("/add")) products.create(product, categoryId);
            else { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            response.sendRedirect(request.getContextPath() + "/admin/products");
        } catch (IllegalArgumentException exception) { form(request, response, null, exception.getMessage()); }
    }

    private Product bind(HttpServletRequest request) {
        Product product = new Product();
        product.setName(RequestValidator.required(request.getParameter("name"), "Tên sản phẩm", 255));
        product.setDescription(RequestValidator.optional(request.getParameter("description"), "Mô tả", 2000));
        product.setImage(RequestValidator.httpUrl(request.getParameter("image"), "URL ảnh"));
        product.setPrice(RequestValidator.nonNegativeDecimal(request.getParameter("price"), "Giá"));
        String status = request.getParameter("status");
        if (!"0".equals(status) && !"1".equals(status)) throw new IllegalArgumentException("Trạng thái sản phẩm không hợp lệ.");
        product.setStatus(Integer.valueOf(status));
        return product;
    }

    private void form(HttpServletRequest request, HttpServletResponse response, Product product, String alert) throws ServletException, IOException { request.setAttribute("product", product); request.setAttribute("categories", categories.findAll()); request.setAttribute("alert", alert); request.getRequestDispatcher("/views/admin/product-form.jsp").forward(request, response); }
}
