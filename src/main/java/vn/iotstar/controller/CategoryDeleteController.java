package vn.iotstar.controller;

import java.io.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.RequestValidator;

@WebServlet("/admin/category/delete")
public class CategoryDeleteController extends HttpServlet {
    private final CategoryService service = new CategoryServiceImpl();

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int categoryId = RequestValidator.positiveId(req.getParameter("id"), "ID danh mục");
            Category category = service.findById(categoryId);
            if (category == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            service.delete(category.getCategoryId());
            if (category.getImages() != null) {
                File image = new File(Constant.DIR, category.getImages());
                if (image.exists()) image.delete();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } catch (IllegalArgumentException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (RuntimeException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xóa danh mục.");
        }
    }
}
