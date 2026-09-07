package vn.iotstar.service.impl;

import vn.iotstar.dao.UserProfileDao;
import vn.iotstar.dao.impl.UserProfileDaoImpl;
import vn.iotstar.entity.UserProfile;
import vn.iotstar.service.ProfileService;

public class ProfileServiceImpl implements ProfileService {
    private final UserProfileDao profiles = new UserProfileDaoImpl();

    @Override public UserProfile findById(int id) { return profiles.findById(id); }

    @Override
    public void update(int id, String fullName, String phone, String avatar) {
        String normalizedName = required(fullName, "Họ và tên không được để trống.");
        String normalizedPhone = normalizePhone(phone);
        profiles.update(id, normalizedName, normalizedPhone, avatar);
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    private String normalizePhone(String value) {
        if (value == null || value.isBlank()) return null;
        String phone = value.trim();
        if (!phone.matches("[0-9+() .-]{8,30}")) throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        return phone;
    }
}
