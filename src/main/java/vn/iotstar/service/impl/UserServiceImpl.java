package vn.iotstar.service.impl;
import java.sql.Date;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.PasswordUtil;
public class UserServiceImpl implements UserService {
    private final UserDao dao=new UserDaoImpl();
    public User login(String username,String password){User user=dao.get(username);if(user==null||!PasswordUtil.matches(password,user.getPassWord()))return null;if(PasswordUtil.needsUpgrade(user.getPassWord())){user.setPassWord(PasswordUtil.hash(password));dao.updatePassword(user.getEmail(),user.getPassWord());}return user;}
    public User get(String username){return dao.get(username);} public User getByEmail(String email){return dao.getByEmail(email);} public User getById(int id){return dao.getById(id);} public java.util.List<User> findAll(){return dao.findAll();}
    public void insert(User user){dao.insert(user);} public boolean checkExistEmail(String v){return dao.checkExistEmail(v);} public boolean checkExistUsername(String v){return dao.checkExistUsername(v);} public boolean checkExistPhone(String v){return dao.checkExistPhone(v);}
    public boolean register(String username,String password,String email,String fullname,String phone){if(checkExistEmail(email)||checkExistUsername(username)||(!phone.isBlank()&&checkExistPhone(phone)))return false;User user=new User(email,username,fullname,PasswordUtil.hash(password),null,5,phone,Date.valueOf(java.time.LocalDate.now()));user.setActive(false);dao.insert(user);return true;}
    public void activate(String email){dao.activate(email);} public void resetPassword(String email,String password){dao.updatePassword(email,PasswordUtil.hash(password));} public void setEnabled(int id, boolean enabled){dao.setEnabled(id,enabled);}
}
