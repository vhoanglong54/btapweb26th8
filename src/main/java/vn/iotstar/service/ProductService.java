package vn.iotstar.service;

import java.util.List;
import vn.iotstar.entity.Product;

public interface ProductService { void create(Product product, int categoryId); void update(Product product, int categoryId); void delete(int id); Product findById(int id); List<Product> findPage(int page, int pageSize); List<Product> findAllForAdmin(); List<Product> newest(int limit); long count(); }
