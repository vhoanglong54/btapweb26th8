package vn.iotstar.dao.impl;

import jakarta.persistence.*;
import java.util.List;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.ProductDao;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;

public class ProductDaoImpl implements ProductDao {
    @Override public void insert(Product product) { transaction(em -> { product.setCategory(em.getReference(Category.class, product.getCategory().getCategoryId())); em.persist(product); }); }
    @Override public void update(Product product) { transaction(em -> { int categoryId = product.getCategory().getCategoryId(); Product managed = em.merge(product); managed.setCategory(em.getReference(Category.class, categoryId)); }); }
    @Override public void delete(int id) { transaction(em -> { Product product = em.find(Product.class, id); if (product != null) em.remove(product); }); }
    @Override public Product findById(int id) { try (EntityManager em = JpaConfig.getEntityManager()) { return em.find(Product.class, id); } }
    @Override public List<Product> findAll(int page, int pageSize) { return query(page, pageSize, "SELECT p FROM Product p WHERE p.status=1 ORDER BY p.createdAt DESC, p.productId DESC"); }
    @Override public List<Product> findAllForAdmin() { try (EntityManager em = JpaConfig.getEntityManager()) { return em.createQuery("SELECT p FROM Product p ORDER BY p.createdAt DESC, p.productId DESC", Product.class).getResultList(); } }
    @Override public List<Product> findNewest(int limit) { return query(1, limit, "SELECT p FROM Product p WHERE p.status=1 ORDER BY p.createdAt DESC, p.productId DESC"); }
    @Override public long count() { try (EntityManager em = JpaConfig.getEntityManager()) { return em.createQuery("SELECT COUNT(p) FROM Product p WHERE p.status=1", Long.class).getSingleResult(); } }
    private List<Product> query(int page, int pageSize, String jpql) { if (page < 1 || pageSize < 1) throw new IllegalArgumentException("Trang không hợp lệ"); try (EntityManager em = JpaConfig.getEntityManager()) { return em.createQuery(jpql, Product.class).setFirstResult((page - 1) * pageSize).setMaxResults(pageSize).getResultList(); } }
    private void transaction(Work work) { try (EntityManager em = JpaConfig.getEntityManager()) { EntityTransaction tx = em.getTransaction(); try { tx.begin(); work.run(em); tx.commit(); } catch (RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; } } }
    @FunctionalInterface private interface Work { void run(EntityManager manager); }
}
