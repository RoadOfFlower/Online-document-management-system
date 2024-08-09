package filesystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            // 进行数据库连接的初始化
            String url = "jdbc:mysql://localhost:3306/filemanagement?useUnicode=true&useSSL=false&characterEncoding=UTF-8";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
