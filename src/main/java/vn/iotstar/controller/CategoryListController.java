package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.RequestValidator;

@WebServlet({"/admin/categories", "/admin/category/list"})
public class CategoryListController extends HttpServlet {
    private final CategoryService service = new CategoryServiceImpl();
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String keyword = RequestValidator.optional(req.getParameter("keyword"), "Từ khóa tìm kiếm", 100);
            req.setAttribute("cateList", keyword == null ? service.findAll() : service.searchByName(keyword));
            req.setAttribute("categoryCount", service.count());
            req.getRequestDispatcher("/views/admin/list-category.jsp").forward(req, resp);
        } catch (IllegalArgumentException exception) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        }
    }
}
