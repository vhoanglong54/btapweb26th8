package vn.iotstar.dao;

import vn.iotstar.model.SmtpSettings;

public interface SmtpSettingsDao {
    SmtpSettings find();
    void save(SmtpSettings settings, boolean replacePassword);
}
