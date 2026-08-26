package vn.iotstar.util;
import java.io.File;
public final class Constant {
    private Constant() { }
    // The PDF uses E:\\upload; this computer has no E: drive, so use the workspace upload folder.
    public static final String DIR = "D:\\btap_web1\\upload";
    public static final String SESSION_ACCOUNT = "account";
    public static final String COOKIE_REMEMBER = "username";
    public static final String LOGIN = "/views/login.jsp";
    public static final String REGISTER = "/views/register.jsp";
    public static File categoryDirectory(){File dir=new File(DIR,"category");if(!dir.exists()&&!dir.mkdirs())throw new IllegalStateException("Không thể tạo thư mục "+dir);return dir;}
}
