package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.RequestValidator;

@WebServlet({"/admin/category/edit", "/admin/category/update"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class CategoryEditController extends HttpServlet {
    private final CategoryService categories = new CategoryServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try { Category category = categories.findById(RequestValidator.positiveId(request.getParameter("id"), "ID danh mục")); if (category == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; } request.setAttribute("category", category); request.getRequestDispatcher("/views/admin/edit-category.jsp").forward(request, response); }
        catch (IllegalArgumentException exception) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage()); }
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            int id = RequestValidator.positiveId(request.getParameter("id"), "ID danh mục");
            Category old = categories.findById(id);
            if (old == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            Category category = new Category(); category.setCategoryId(id); category.setCategoryname(RequestValidator.required(request.getParameter("name"), "Tên danh mục", 100)); category.setStatus(old.getStatus()); category.setImages(old.getImages());
            Part image = request.getPart("icon"); boolean hasNewImage = image != null && image.getSize() > 0;
            if (hasNewImage) category.setImages(CategoryAddController.saveImage(image));
            categories.update(category);
            if (hasNewImage && old.getImages() != null && old.getImages().startsWith("category/")) { File oldImage = new File(Constant.DIR, old.getImages()); if (oldImage.isFile()) oldImage.delete(); }
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } catch (IllegalArgumentException exception) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage()); }
    }
}
