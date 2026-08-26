package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Shared JPA bootstrap for the application. */
public final class JpaConfig {
    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");

    private JpaConfig() {
    }

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

    public static void close() {
        if (FACTORY.isOpen()) {
            FACTORY.close();
        }
    }
}
