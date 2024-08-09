package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import filesystem.util.DBUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "Register", value = "/Register")
public class Register extends HttpServlet {
    private String message;

    public void init() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("1");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();

            // 从请求参数中获取 username 和 password
            String username_ = request.getParameter("username");

            // 构建查询语句
            String query = "SELECT * FROM user WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username_);

            // 执行查询
            resultSet = statement.executeQuery();
            // 处理查询结果
            while (resultSet.next()) {
                JSONObject res = new JSONObject();
                res.put("code", 405);
                response.getWriter().write(String.valueOf(res));
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            connection = DBUtil.getConnection();

            // 从请求参数中获取 username 和 password
            String username_ = request.getParameter("username");
            String password_ = request.getParameter("password");

            // 构建查询语句
            String query = "INSERT INTO user (username, password) values (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, username_);
            statement.setString(2, password_);

            // 执行查询
            statement.executeUpdate();

            JSONObject res = new JSONObject();
            res.put("code", 200);
            response.getWriter().write(String.valueOf(res));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}