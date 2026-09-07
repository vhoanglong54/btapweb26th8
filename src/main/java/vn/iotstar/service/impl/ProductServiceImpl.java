package vn.iotstar.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import vn.iotstar.dao.ProductDao;
import vn.iotstar.dao.impl.ProductDaoImpl;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final ProductDao products = new ProductDaoImpl(); private final CategoryService categories = new CategoryServiceImpl();
    @Override public void create(Product product, int categoryId) { validate(product); product.setCategory(category(categoryId)); product.setCreatedAt(LocalDateTime.now()); products.insert(product); }
    @Override public void update(Product product, int categoryId) { if (products.findById(product.getProductId()) == null) throw new IllegalArgumentException("Sản phẩm không tồn tại"); validate(product); product.setCategory(category(categoryId)); products.update(product); }
    @Override public void delete(int id) { products.delete(id); } @Override public Product findById(int id) { return products.findById(id); }
    @Override public List<Product> findPage(int page, int pageSize) { return products.findAll(page, pageSize); } @Override public List<Product> findAllForAdmin() { return products.findAllForAdmin(); } @Override public List<Product> newest(int limit) { return products.findNewest(limit); } @Override public long count() { return products.count(); }
    private Category category(int id) { Category category = categories.findById(id); if (category == null) throw new IllegalArgumentException("Danh mục không tồn tại"); return category; }
    private void validate(Product p) { if (p.getName() == null || p.getName().isBlank()) throw new IllegalArgumentException("Tên sản phẩm không được rỗng"); if (p.getPrice() == null || p.getPrice().signum() < 0) throw new IllegalArgumentException("Giá phải là số không âm"); if (p.getStatus() == null) p.setStatus(1); }
}
