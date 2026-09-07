package vn.iotstar.dao.impl;

import java.sql.*;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.OtpDao;

public class OtpDaoImpl extends DBConnection implements OtpDao {
    @Override public void replace(String email, String purpose, String codeHash) {
        try (Connection cn = getConnection()) { cn.setAutoCommit(false);
            try (PreparedStatement expire = cn.prepareStatement("UPDATE UserOtp SET consumedAt=SYSUTCDATETIME() WHERE email=? AND purpose=? AND consumedAt IS NULL"); PreparedStatement insert = cn.prepareStatement("INSERT INTO UserOtp(email,purpose,codeHash,expiresAt) VALUES(?,?,?,DATEADD(MINUTE,10,SYSUTCDATETIME()))")) {
                expire.setString(1, email); expire.setString(2, purpose); expire.executeUpdate();
                insert.setString(1, email); insert.setString(2, purpose); insert.setString(3, codeHash); insert.executeUpdate(); cn.commit();
            } catch (SQLException e) { cn.rollback(); throw e; }
        } catch (Exception e) { throw new IllegalStateException("Không thể tạo OTP", e); }
    }
    @Override public boolean consume(String email, String purpose, String codeHash) {
        String failed = "UPDATE UserOtp SET attempts=attempts+1 WHERE id=(SELECT TOP 1 id FROM UserOtp WHERE email=? AND purpose=? AND codeHash<>? AND consumedAt IS NULL AND expiresAt>SYSUTCDATETIME() AND attempts<5 ORDER BY id DESC)";
        String sql = "UPDATE UserOtp SET consumedAt=SYSUTCDATETIME() WHERE id=(SELECT TOP 1 id FROM UserOtp WHERE email=? AND purpose=? AND codeHash=? AND consumedAt IS NULL AND expiresAt>SYSUTCDATETIME() AND attempts<5 ORDER BY id DESC)";
        try (Connection cn = getConnection(); PreparedStatement wrong = cn.prepareStatement(failed); PreparedStatement ps = cn.prepareStatement(sql)) { wrong.setString(1, email); wrong.setString(2, purpose); wrong.setString(3, codeHash); wrong.executeUpdate(); ps.setString(1, email); ps.setString(2, purpose); ps.setString(3, codeHash); return ps.executeUpdate() == 1; }
        catch (Exception e) { throw new IllegalStateException("Không thể xác thực OTP", e); }
    }
}
