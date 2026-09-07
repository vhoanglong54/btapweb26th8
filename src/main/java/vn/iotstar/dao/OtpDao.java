package vn.iotstar.dao;

public interface OtpDao {
    void replace(String email, String purpose, String codeHash);
    boolean consume(String email, String purpose, String codeHash);
}
