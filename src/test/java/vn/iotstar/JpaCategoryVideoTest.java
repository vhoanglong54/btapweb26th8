package vn.iotstar;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

/** Manual JPA relationship smoke test for a local/disposable database. */
public final class JpaCategoryVideoTest {
    private JpaCategoryVideoTest() { }

    public static void main(String[] args) {
        EntityManager entityManager = JpaConfig.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (entityManager.find(Video.class, "v01") == null) {
                Category category = new Category("Iphone", "abc.jpg", 1);
                Video video = new Video();
                video.setVideoId("v01");
                video.setTitle("test");
                category.addVideo(video);
                entityManager.persist(category);
                entityManager.persist(video);
            }
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) transaction.rollback();
            throw exception;
        } finally {
            entityManager.close();
            JpaConfig.close();
        }
    }
}
