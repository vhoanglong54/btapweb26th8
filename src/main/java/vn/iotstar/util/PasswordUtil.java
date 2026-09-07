package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2 password hashing with support for existing plaintext exercise accounts. */
public final class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private PasswordUtil() { }

    public static String hash(String password) {
        byte[] salt = new byte[16]; RANDOM.nextBytes(salt);
        return "pbkdf2$120000$" + Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(derive(password.toCharArray(), salt));
    }
    public static boolean matches(String password, String stored) {
        if (stored == null) return false;
        if (!stored.startsWith("pbkdf2$")) return constantEquals(password.getBytes(StandardCharsets.UTF_8), stored.getBytes(StandardCharsets.UTF_8));
        String[] parts = stored.split("\\$");
        if (parts.length != 4) return false;
        return constantEquals(derive(password.toCharArray(), Base64.getDecoder().decode(parts[2])), Base64.getDecoder().decode(parts[3]));
    }
    public static boolean needsUpgrade(String stored) { return stored == null || !stored.startsWith("pbkdf2$"); }
    private static byte[] derive(char[] password, byte[] salt) { try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(password, salt, 120000, 256)).getEncoded(); } catch (Exception e) { throw new IllegalStateException("Không thể mã hóa mật khẩu", e); } }
    private static boolean constantEquals(byte[] a, byte[] b) { return MessageDigest.isEqual(a, b); }
}
