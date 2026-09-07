package vn.iotstar.util;
import java.io.File;
public final class Constant {
    private Constant() { }
    /** Override with APP_UPLOAD_DIR system property or environment variable when deploying. */
    public static final String DIR = uploadDirectory();
    public static final String SESSION_ACCOUNT = "account";
    public static final String COOKIE_REMEMBER = "username";
    public static final String LOGIN = "/views/login.jsp";
    public static final String REGISTER = "/views/register.jsp";
    public static File categoryDirectory(){File dir=new File(DIR,"category");if(!dir.exists()&&!dir.mkdirs())throw new IllegalStateException("Không thể tạo thư mục "+dir);return dir;}
    private static String uploadDirectory() { String configured = System.getProperty("APP_UPLOAD_DIR"); if (configured == null || configured.isBlank()) configured = System.getenv("APP_UPLOAD_DIR"); return configured == null || configured.isBlank() ? new File(System.getProperty("user.home"), "ServletCRUDMVC/upload").getPath() : configured; }
}
