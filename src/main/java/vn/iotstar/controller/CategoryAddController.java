package vn.iotstar.controller;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet({"/admin/category/add", "/admin/category/insert"})
@MultipartConfig
public class CategoryAddController extends HttpServlet {
    private final CategoryService service = new CategoryServiceImpl();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { req.getRequestDispatcher("/views/admin/add-category.jsp").forward(req, resp); }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Category category = new Category();
        try {
            category.setCategoryname(req.getParameter("name").trim());
            category.setStatus(1);
            Part imagePart = req.getPart("icon");
            if (imagePart != null && imagePart.getSize() > 0) category.setImages(saveImage(imagePart));
            if (category.getName() == null || category.getName().isEmpty()) { error(req, resp, "Tên danh mục không được rỗng"); return; }
            service.insert(category); resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
        catch (Exception e) { throw new ServletException("Không thể thêm danh mục", e); }
    }
    static String saveImage(Part part) throws IOException {
        String original = new File(part.getSubmittedFileName()).getName(); int dot = original.lastIndexOf('.');
        if (dot < 1) throw new IllegalArgumentException("Ảnh phải có phần mở rộng");
        String extension = original.substring(dot + 1).toLowerCase();
        if (!extension.matches("png|jpe?g|gif|webp")) throw new IllegalArgumentException("Chỉ nhận PNG, JPG, GIF hoặc WEBP");
        String name = System.currentTimeMillis() + "-" + java.util.UUID.randomUUID() + "." + extension;
        File target = new File(Constant.categoryDirectory(), name);
        part.write(target.getAbsolutePath());
        return "category/" + name;
    }
    private void error(HttpServletRequest req, HttpServletResponse resp, String message) throws ServletException, IOException { req.setAttribute("alert", message); req.getRequestDispatcher("/views/admin/add-category.jsp").forward(req, resp); }
}
