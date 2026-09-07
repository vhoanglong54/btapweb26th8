package vn.iotstar.controller;

import java.io.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

@WebServlet("/admin/category/delete")
public class CategoryDeleteController extends HttpServlet {
    private final CategoryService service = new CategoryServiceImpl();
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try { Category category=service.findById(Integer.parseInt(req.getParameter("id"))); if(category==null){resp.sendError(404);return;} service.delete(category.getCategoryId()); if(category.getImages()!=null){File image=new File(Constant.DIR, category.getImages());if(image.exists())image.delete();} resp.sendRedirect(req.getContextPath()+"/admin/categories"); }
        catch (RuntimeException e) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID danh mục không hợp lệ"); }
    }
}
