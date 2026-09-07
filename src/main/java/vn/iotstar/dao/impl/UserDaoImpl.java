package vn.iotstar.dao.impl;

import java.sql.*;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.UserDao;
import vn.iotstar.model.User;

public class UserDaoImpl extends DBConnection implements UserDao {
    @Override public User get(String username) { return findOne("SELECT * FROM [User] WHERE username=?", username); }
    @Override public User getByEmail(String email) { return findOne("SELECT * FROM [User] WHERE email=?", email); }
    @Override public User getById(int id) { return findOne("SELECT * FROM [User] WHERE id=?", id); }

    @Override public java.util.List<User> findAll() {
        java.util.List<User> users = new java.util.ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM [User] ORDER BY createdDate DESC, id DESC"); ResultSet result = statement.executeQuery()) {
            while (result.next()) users.add(map(result));
            return users;
        } catch (Exception exception) { throw new IllegalStateException("Không thể đọc danh sách người dùng", exception); }
    }

    @Override public void insert(User user) {
        String sql = "INSERT INTO [User](email,username,fullname,password,avatar,roleid,phone,createdDate,active,enabled) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail()); statement.setString(2, user.getUserName()); statement.setString(3, user.getFullName()); statement.setString(4, user.getPassWord()); statement.setString(5, user.getAvatar()); statement.setInt(6, user.getRoleid()); statement.setString(7, user.getPhone()); statement.setDate(8, user.getCreatedDate()); statement.setBoolean(9, user.isActive()); statement.setBoolean(10, user.isEnabled()); statement.executeUpdate();
        } catch (Exception exception) { throw new IllegalStateException("Không thể thêm User", exception); }
    }

    @Override public void activate(String email) { execute("UPDATE [User] SET active=1 WHERE email=?", email, null); }
    @Override public void updatePassword(String email, String password) { execute("UPDATE [User] SET password=? WHERE email=?", email, password); }
    @Override public void setEnabled(int id, boolean enabled) { execute("UPDATE [User] SET enabled=? WHERE id=?", enabled, id); }

    @Override public boolean checkExistEmail(String value) { return exists("email", value); }
    @Override public boolean checkExistUsername(String value) { return exists("username", value); }
    @Override public boolean checkExistPhone(String value) { return exists("phone", value); }

    private User findOne(String sql, Object value) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (value instanceof Integer id) statement.setInt(1, id); else statement.setString(1, (String) value);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? map(result) : null; }
        } catch (Exception exception) { throw new IllegalStateException("Không thể đọc User", exception); }
    }

    private User map(ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getInt("id")); user.setEmail(result.getString("email")); user.setUserName(result.getString("username")); user.setFullName(result.getString("fullname")); user.setPassWord(result.getString("password")); user.setAvatar(result.getString("avatar")); user.setRoleid(result.getInt("roleid")); user.setPhone(result.getString("phone")); user.setCreatedDate(result.getDate("createdDate")); user.setActive(result.getBoolean("active")); user.setEnabled(result.getBoolean("enabled"));
        return user;
    }

    private void execute(String sql, String email, String password) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (password == null) statement.setString(1, email); else { statement.setString(1, password); statement.setString(2, email); }
            statement.executeUpdate();
        } catch (Exception exception) { throw new IllegalStateException("Không thể cập nhật User", exception); }
    }

    private void execute(String sql, boolean enabled, int id) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, enabled); statement.setInt(2, id); statement.executeUpdate();
        } catch (Exception exception) { throw new IllegalStateException("Không thể cập nhật trạng thái User", exception); }
    }

    private boolean exists(String field, String value) {
        String sql = "SELECT 1 FROM [User] WHERE " + field + "=?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value); try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (Exception exception) { throw new IllegalStateException("Không thể kiểm tra User", exception); }
    }
}
