package vn.iotstar.service;

import java.util.List;
import vn.iotstar.entity.Category;

public interface CategoryService {
    void insert(Category category);
    void update(Category category);
    void delete(int categoryId);
    Category findById(int categoryId);
    Category findByCategoryname(String name);
    List<Category> findAll();
    List<Category> searchByName(String name);
    List<Category> findAll(int page, int pageSize);
    long count();
}
