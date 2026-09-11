package vn.iotstar.service.impl;

import vn.iotstar.dao.UserProfileDao;
import vn.iotstar.dao.impl.UserProfileDaoImpl;
import vn.iotstar.entity.UserProfile;
import vn.iotstar.service.ProfileService;
import vn.iotstar.util.RequestValidator;

public class ProfileServiceImpl implements ProfileService {
    private final UserProfileDao profiles = new UserProfileDaoImpl();

    @Override public UserProfile findById(int id) { return profiles.findById(id); }

    @Override
    public void update(int id, String fullName, String phone, String avatar) {
        String normalizedName = RequestValidator.required(fullName, "Họ và tên", 100);
        if (normalizedName.length() < 2) throw new IllegalArgumentException("Họ và tên phải có ít nhất 2 ký tự.");
        String normalizedPhone = RequestValidator.phone(phone);
        profiles.update(id, normalizedName, normalizedPhone, avatar);
    }
}
