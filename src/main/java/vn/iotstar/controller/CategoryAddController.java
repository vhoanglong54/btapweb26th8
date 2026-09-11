package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
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

@WebServlet({"/admin/category/add", "/admin/category/insert"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class CategoryAddController extends HttpServlet {
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private final CategoryService categories = new CategoryServiceImpl();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { request.getRequestDispatcher("/views/admin/add-category.jsp").forward(request, response); }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            Category category = new Category();
            category.setCategoryname(RequestValidator.required(request.getParameter("name"), "Tên danh mục", 100));
            category.setStatus(1);
            Part image = request.getPart("icon");
            if (image != null && image.getSize() > 0) category.setImages(saveImage(image));
            categories.insert(category);
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        } catch (IllegalArgumentException exception) { error(request, response, exception.getMessage()); }
    }

    static String saveImage(Part part) throws IOException {
        String submittedName = part.getSubmittedFileName() == null ? "" : new File(part.getSubmittedFileName()).getName();
        int dot = submittedName.lastIndexOf('.');
        if (dot < 1) throw new IllegalArgumentException("Ảnh phải có phần mở rộng hợp lệ.");
        String extension = submittedName.substring(dot + 1).toLowerCase();
        if (!extension.matches("png|jpe?g|gif|webp") || !IMAGE_TYPES.contains(part.getContentType())) throw new IllegalArgumentException("Chỉ nhận ảnh PNG, JPG, GIF hoặc WEBP.");
        String name = UUID.randomUUID() + "." + extension;
        part.write(new File(Constant.categoryDirectory(), name).getAbsolutePath());
        return "category/" + name;
    }

    private void error(HttpServletRequest request, HttpServletResponse response, String message) throws ServletException, IOException { request.setAttribute("alert", message); request.getRequestDispatcher("/views/admin/add-category.jsp").forward(request, response); }
}
