package vn.iotstar.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Small, framework-free validation helpers for request data.
 *
 * Controllers use this class before calling a service so malformed input is
 * returned to the same form instead of becoming a parsing or database error.
 */
public final class RequestValidator {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{3,50}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+() .-]{8,30}$");
    private static final Pattern OTP = Pattern.compile("^\\d{6}$");
    private static final Pattern HTTP_URL = Pattern.compile("^https?://.+", Pattern.CASE_INSENSITIVE);

    private RequestValidator() { }

    public static String required(String value, String label, int maxLength) {
        String normalized = trim(value);
        if (normalized == null) throw new IllegalArgumentException(label + " không được để trống.");
        if (normalized.length() > maxLength) throw new IllegalArgumentException(label + " tối đa " + maxLength + " ký tự.");
        return normalized;
    }

    public static String optional(String value, String label, int maxLength) {
        String normalized = trim(value);
        if (normalized != null && normalized.length() > maxLength) throw new IllegalArgumentException(label + " tối đa " + maxLength + " ký tự.");
        return normalized;
    }

    public static String username(String value) {
        String username = required(value, "Tài khoản", 50);
        if (!USERNAME.matcher(username).matches()) throw new IllegalArgumentException("Tài khoản gồm 3-50 ký tự: chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.");
        return username;
    }

    public static String email(String value) {
        String email = required(value, "Email", 254);
        if (!EMAIL.matcher(email).matches()) throw new IllegalArgumentException("Email không hợp lệ.");
        return email;
    }

    public static String phone(String value) {
        String phone = optional(value, "Số điện thoại", 30);
        if (phone != null && !PHONE.matcher(phone).matches()) throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        return phone;
    }

    public static String password(String value, String label) {
        if (value == null || value.length() < 8 || value.length() > 128) {
            throw new IllegalArgumentException(label + " phải có từ 8 đến 128 ký tự.");
        }
        return value;
    }

    public static String otp(String value) {
        String otp = trim(value);
        if (otp == null || !OTP.matcher(otp).matches()) throw new IllegalArgumentException("OTP phải gồm đúng 6 chữ số.");
        return otp;
    }

    public static int positiveId(String value, String label) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) { }
        throw new IllegalArgumentException(label + " không hợp lệ.");
    }

    public static BigDecimal nonNegativeDecimal(String value, String label) {
        try {
            BigDecimal amount = new BigDecimal(required(value, label, 30));
            if (amount.signum() >= 0 && amount.scale() <= 2) return amount;
        } catch (NumberFormatException ignored) { }
        throw new IllegalArgumentException(label + " phải là số không âm, tối đa 2 chữ số thập phân.");
    }

    public static String httpUrl(String value, String label) {
        String url = optional(value, label, 2048);
        if (url != null && !HTTP_URL.matcher(url).matches()) throw new IllegalArgumentException(label + " phải bắt đầu bằng http:// hoặc https://.");
        return url;
    }

    public static String trim(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
