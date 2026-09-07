package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class OtpUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private OtpUtil() { }
    public static String generate() { return String.format("%06d", RANDOM.nextInt(1_000_000)); }
    public static String hash(String value) { try { byte[] bytes = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)); StringBuilder result = new StringBuilder(64); for (byte b : bytes) result.append(String.format("%02x", b)); return result.toString(); } catch (Exception e) { throw new IllegalStateException("Không thể mã hóa OTP", e); } }
}
