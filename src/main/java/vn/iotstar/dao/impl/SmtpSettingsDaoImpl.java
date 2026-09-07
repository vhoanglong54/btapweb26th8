package vn.iotstar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.SmtpSettingsDao;
import vn.iotstar.model.SmtpSettings;

/** Stores one local SMTP profile. Password is never returned to a browser. */
public class SmtpSettingsDaoImpl extends DBConnection implements SmtpSettingsDao {
    @Override
    public SmtpSettings find() {
        String sql = "SELECT host, port, username, password, fromEmail, startTls FROM SmtpSettings WHERE id=1";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            if (!result.next()) return null;
            SmtpSettings settings = new SmtpSettings();
            settings.setHost(result.getString("host"));
            settings.setPort(result.getInt("port"));
            settings.setUsername(result.getString("username"));
            settings.setPassword(result.getString("password"));
            settings.setFromEmail(result.getString("fromEmail"));
            settings.setStartTls(result.getBoolean("startTls"));
            return settings;
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể đọc cấu hình SMTP. Hãy chạy lại file database/ServletCRUDMVC.sql.", exception);
        }
    }

    @Override
    public void save(SmtpSettings settings, boolean replacePassword) {
        String update = replacePassword
                ? "UPDATE SmtpSettings SET host=?, port=?, username=?, password=?, fromEmail=?, startTls=?, updatedAt=SYSUTCDATETIME() WHERE id=1"
                : "UPDATE SmtpSettings SET host=?, port=?, username=?, fromEmail=?, startTls=?, updatedAt=SYSUTCDATETIME() WHERE id=1";
        String insert = "INSERT INTO SmtpSettings(id, host, port, username, password, fromEmail, startTls) VALUES(1,?,?,?,?,?,?)";
        try (Connection connection = getConnection()) {
            int updated;
            try (PreparedStatement statement = connection.prepareStatement(update)) {
                bind(statement, settings, replacePassword);
                updated = statement.executeUpdate();
            }
            if (updated == 0) {
                try (PreparedStatement statement = connection.prepareStatement(insert)) {
                    bindInsert(statement, settings);
                    statement.executeUpdate();
                }
            }
        } catch (Exception exception) {
            throw new IllegalStateException("Không thể lưu cấu hình SMTP. Hãy chạy lại file database/ServletCRUDMVC.sql.", exception);
        }
    }

    private void bind(PreparedStatement statement, SmtpSettings settings, boolean replacePassword) throws Exception {
        statement.setString(1, settings.getHost());
        statement.setInt(2, settings.getPort());
        statement.setString(3, settings.getUsername());
        int fromIndex;
        if (replacePassword) { statement.setString(4, settings.getPassword()); fromIndex = 5; }
        else fromIndex = 4;
        statement.setString(fromIndex, settings.getFromEmail());
        statement.setBoolean(fromIndex + 1, settings.isStartTls());
    }

    private void bindInsert(PreparedStatement statement, SmtpSettings settings) throws Exception {
        statement.setString(1, settings.getHost());
        statement.setInt(2, settings.getPort());
        statement.setString(3, settings.getUsername());
        statement.setString(4, settings.getPassword());
        statement.setString(5, settings.getFromEmail());
        statement.setBoolean(6, settings.isStartTls());
    }
}
