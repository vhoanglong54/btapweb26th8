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

@WebServlet({"/admin/category/edit", "/admin/category/update"})
@MultipartConfig
public class CategoryEditController extends HttpServlet {
    private final CategoryService service = new CategoryServiceImpl();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try { Category category = service.findById(Integer.parseInt(req.getParameter("id"))); if(category == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; } req.setAttribute("category", category); req.getRequestDispatcher("/views/admin/edit-category.jsp").forward(req, resp); }
        catch (RuntimeException e) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID danh mục không hợp lệ"); }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Category category = new Category();
        try {
            category.setCategoryId(Integer.parseInt(req.getParameter("id")));
            category.setCategoryname(req.getParameter("name").trim());
            if (category.getName() == null || category.getName().isEmpty()) { resp.sendError(400, "Tên danh mục không được rỗng"); return; }
            Category old = service.findById(category.getCategoryId()); if (old == null) { resp.sendError(404); return; }
            category.setImages(old.getImages());
            category.setStatus(old.getStatus());
            Part imagePart = req.getPart("icon");
            boolean hasNewImage = imagePart != null && imagePart.getSize() > 0;
            if (hasNewImage) category.setImages(CategoryAddController.saveImage(imagePart));
            service.update(category);
            if (hasNewImage && old.getImages() != null) {
                File oldImage = new File(Constant.DIR, old.getImages());
                if (oldImage.exists()) oldImage.delete();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
        catch (Exception e) { throw new ServletException("Không thể sửa danh mục", e); }
    }
}
