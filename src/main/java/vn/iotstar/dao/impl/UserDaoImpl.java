package vn.iotstar.dao.impl;

import java.sql.*;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.UserDao;
import vn.iotstar.model.User;

public class UserDaoImpl extends DBConnection implements UserDao {
    public User get(String username) { String sql="SELECT * FROM [User] WHERE username=?"; try(Connection cn=getConnection(); PreparedStatement ps=cn.prepareStatement(sql)){ps.setString(1,username);try(ResultSet rs=ps.executeQuery()){if(!rs.next())return null; User u=new User();u.setId(rs.getInt("id"));u.setEmail(rs.getString("email"));u.setUserName(rs.getString("username"));u.setFullName(rs.getString("fullname"));u.setPassWord(rs.getString("password"));u.setAvatar(rs.getString("avatar"));u.setRoleid(rs.getInt("roleid"));u.setPhone(rs.getString("phone"));u.setCreatedDate(rs.getDate("createdDate"));return u;}}catch(Exception e){throw new IllegalStateException("Không thể đọc User",e);} }
    public void insert(User u) { String sql="INSERT INTO [User](email,username,fullname,password,avatar,roleid,phone,createdDate) VALUES (?,?,?,?,?,?,?,?)"; try(Connection cn=getConnection();PreparedStatement ps=cn.prepareStatement(sql)){ps.setString(1,u.getEmail());ps.setString(2,u.getUserName());ps.setString(3,u.getFullName());ps.setString(4,u.getPassWord());ps.setString(5,u.getAvatar());ps.setInt(6,u.getRoleid());ps.setString(7,u.getPhone());ps.setDate(8,u.getCreatedDate());ps.executeUpdate();}catch(Exception e){throw new IllegalStateException("Không thể thêm User",e);} }
    public boolean checkExistEmail(String value){return exists("email",value);} public boolean checkExistUsername(String value){return exists("username",value);} public boolean checkExistPhone(String value){return exists("phone",value);}
    private boolean exists(String field,String value){String sql="SELECT 1 FROM [User] WHERE "+field+"=?";try(Connection cn=getConnection();PreparedStatement ps=cn.prepareStatement(sql)){ps.setString(1,value);try(ResultSet rs=ps.executeQuery()){return rs.next();}}catch(Exception e){throw new IllegalStateException("Không thể kiểm tra User",e);}}
}
