package vn.iotstar.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/** JDBC SQL Server configuration requested in the exercise. */
public class DBConnection {
    private static final String SERVER = "127.0.0.1";
    private static final String PORT = "1433";
    private static final String DATABASE = "ServletCRUDMVC";
    // SQL Server is exposed by the local direct TCP endpoint.
    private static final String INSTANCE = "";
    private static final String USER = "vuhoanglong";
    private static final String PASSWORD = "vuhoanglong";

    public Connection getConnection() throws Exception {
        String endpoint = INSTANCE.isBlank() ? SERVER + ":" + PORT : SERVER + "\\" + INSTANCE;
        String url = "jdbc:sqlserver://" + endpoint + ";databaseName=" + DATABASE + ";encrypt=false";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, USER, PASSWORD);
    }
}
