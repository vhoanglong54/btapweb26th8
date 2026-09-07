package vn.iotstar.dao;

import vn.iotstar.entity.UserProfile;

public interface UserProfileDao {
    UserProfile findById(int id);
    void update(int id, String fullName, String phone, String avatar);
}
