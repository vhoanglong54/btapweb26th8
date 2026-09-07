package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.config.JpaConfig;
import vn.iotstar.dao.UserProfileDao;
import vn.iotstar.entity.UserProfile;

/** JPA persistence for fields a user is allowed to edit in their profile. */
public class UserProfileDaoImpl implements UserProfileDao {
    @Override
    public UserProfile findById(int id) {
        try (EntityManager manager = JpaConfig.getEntityManager()) {
            return manager.find(UserProfile.class, id);
        }
    }

    @Override
    public void update(int id, String fullName, String phone, String avatar) {
        try (EntityManager manager = JpaConfig.getEntityManager()) {
            EntityTransaction transaction = manager.getTransaction();
            try {
                transaction.begin();
                UserProfile profile = manager.find(UserProfile.class, id);
                if (profile == null) throw new IllegalArgumentException("Không tìm thấy tài khoản.");
                profile.setFullName(fullName);
                profile.setPhone(phone);
                if (avatar != null) profile.setAvatar(avatar);
                transaction.commit();
            } catch (RuntimeException exception) {
                if (transaction.isActive()) transaction.rollback();
                throw exception;
            }
        }
    }
}
