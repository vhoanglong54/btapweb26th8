package vn.iotstar.service.impl;

import vn.iotstar.dao.OtpDao;
import vn.iotstar.dao.impl.OtpDaoImpl;
import vn.iotstar.service.MailService;
import vn.iotstar.service.OtpService;
import vn.iotstar.util.OtpUtil;

public class OtpServiceImpl implements OtpService {
    private final OtpDao dao = new OtpDaoImpl(); private final MailService mail = new SmtpMailService();
    @Override public void send(String email, String purpose) { String code = OtpUtil.generate(); dao.replace(email, purpose, OtpUtil.hash(code)); mail.sendOtp(email, purpose, code); }
    @Override public boolean verify(String email, String purpose, String code) { return code != null && code.matches("\\d{6}") && dao.consume(email, purpose, OtpUtil.hash(code)); }
}
