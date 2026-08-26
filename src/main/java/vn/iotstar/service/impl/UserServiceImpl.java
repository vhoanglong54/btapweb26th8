package vn.iotstar.service.impl;
import java.sql.Date;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.model.User;
import vn.iotstar.service.UserService;
public class UserServiceImpl implements UserService {
    private final UserDao dao=new UserDaoImpl();
    public User login(String username,String password){User user=dao.get(username);return user!=null&&password.equals(user.getPassWord())?user:null;}
    public User get(String username){return dao.get(username);}
    public void insert(User user){dao.insert(user);} public boolean checkExistEmail(String v){return dao.checkExistEmail(v);} public boolean checkExistUsername(String v){return dao.checkExistUsername(v);} public boolean checkExistPhone(String v){return dao.checkExistPhone(v);}
    public boolean register(String username,String password,String email,String fullname,String phone){if(checkExistEmail(email)||checkExistUsername(username)||(!phone.isBlank()&&checkExistPhone(phone)))return false;dao.insert(new User(email,username,fullname,password,null,5,phone,Date.valueOf(java.time.LocalDate.now())));return true;}
}
