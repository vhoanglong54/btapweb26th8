package vn.iotstar.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.entity.Category;

/** Category repository implemented exclusively with JPA/Hibernate. */
public class CategoryDao implements ICategoryDao {
    @Override
    public void insert(Category category) {
        inTransaction(entityManager -> entityManager.persist(category));
    }

    @Override
    public void update(Category category) {
        inTransaction(entityManager -> entityManager.merge(category));
    }

    @Override
    public void delete(int categoryId) {
        inTransaction(entityManager -> {
            Category category = entityManager.find(Category.class, categoryId);
            if (category != null) {
                entityManager.remove(category);
            }
        });
    }

    @Override
    public Category findById(int categoryId) {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            return entityManager.find(Category.class, categoryId);
        }
    }

    @Override
    public Category findByCategoryname(String name) {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            List<Category> categories = entityManager.createQuery(
                    "SELECT c FROM Category c WHERE c.categoryname = :name", Category.class)
                    .setParameter("name", name).setMaxResults(1).getResultList();
            return categories.isEmpty() ? null : categories.get(0);
        }
    }

    @Override
    public List<Category> findAll() {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            return entityManager.createNamedQuery("Category.findAll", Category.class).getResultList();
        }
    }

    @Override
    public List<Category> searchByName(String name) {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            return entityManager.createQuery(
                    "SELECT c FROM Category c WHERE LOWER(c.categoryname) LIKE LOWER(:name) ORDER BY c.categoryId",
                    Category.class).setParameter("name", "%" + name + "%").getResultList();
        }
    }

    @Override
    public List<Category> findAll(int page, int pageSize) {
        if (page < 1 || pageSize < 1) throw new IllegalArgumentException("page và pageSize phải lớn hơn 0");
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            return entityManager.createNamedQuery("Category.findAll", Category.class)
                    .setFirstResult((page - 1) * pageSize).setMaxResults(pageSize).getResultList();
        }
    }

    @Override
    public long count() {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            return entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();
        }
    }

    private void inTransaction(EntityManagerWork work) {
        try (EntityManager entityManager = JpaConfig.getEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            try {
                transaction.begin();
                work.execute(entityManager);
                transaction.commit();
            } catch (RuntimeException exception) {
                if (transaction.isActive()) transaction.rollback();
                throw exception;
            }
        }
    }

    @FunctionalInterface
    private interface EntityManagerWork { void execute(EntityManager entityManager); }
}
