package vn.iotstar.service;

import vn.iotstar.entity.UserProfile;

public interface ProfileService {
    UserProfile findById(int id);
    void update(int id, String fullName, String phone, String avatar);
}
