package vn.iotstar.service.impl;

import java.util.List;
import vn.iotstar.dao.CategoryDao;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;

public class CategoryServiceImpl implements CategoryService {
    private final ICategoryDao dao = new CategoryDao();

    @Override public void insert(Category category) {
        if (dao.findByCategoryname(category.getCategoryname()) != null) throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        dao.insert(category);
    }
    @Override public void update(Category category) {
        Category existing = dao.findById(category.getCategoryId());
        if (existing == null) throw new IllegalArgumentException("Danh mục không tồn tại");
        Category sameName = dao.findByCategoryname(category.getCategoryname());
        if (sameName != null && sameName.getCategoryId() != category.getCategoryId()) throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        dao.update(category);
    }
    @Override public void delete(int categoryId) { dao.delete(categoryId); }
    @Override public Category findById(int categoryId) { return dao.findById(categoryId); }
    @Override public Category findByCategoryname(String name) { return dao.findByCategoryname(name); }
    @Override public List<Category> findAll() { return dao.findAll(); }
    @Override public List<Category> searchByName(String name) { return dao.searchByName(name); }
    @Override public List<Category> findAll(int page, int pageSize) { return dao.findAll(page, pageSize); }
    @Override public long count() { return dao.count(); }
}
