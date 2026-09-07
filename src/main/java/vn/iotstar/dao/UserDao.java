package vn.iotstar.dao;
import vn.iotstar.model.User;
import java.util.List;
public interface UserDao {
    User get(String username);
    User getByEmail(String email);
    User getById(int id);
    List<User> findAll();
    void insert(User user);
    void activate(String email);
    void updatePassword(String email, String password);
    void setEnabled(int id, boolean enabled);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
}
