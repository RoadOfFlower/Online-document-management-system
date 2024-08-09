package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import filesystem.util.DBUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@WebServlet(name = "Login", value = "/Login")
public class Login extends HttpServlet {
    private String message;

    private static final String SECRET_KEY = "123123";

    public void init() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("1");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.addHeader("Access-Control-Allow-Headers", "Authorization");

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        boolean flag = false;
        String username = null;
        int id = 0;

        try {
            connection = DBUtil.getConnection();

            // 从请求参数中获取 username 和 password
            String username_ = request.getParameter("username");
            String password_ = request.getParameter("password");

            // 构建查询语句
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username_);
            statement.setString(2, password_);

            // 执行查询
            resultSet = statement.executeQuery();



            // 处理查询结果
            while (resultSet.next()) {
                id = resultSet.getInt("id");
                username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String tel = resultSet.getString("tel");

                // 在这里可以根据需要进行进一步的操作，例如打印数据或将数据添加到集合中等
                System.out.println("ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("Email: " + email);
                System.out.println("Tel: " + tel);
//                request.getSession().setAttribute("userid", id);
//                request.getSession().setAttribute("username", username);
//                JSONObject res = new JSONObject();
//                res.put("username", username);
//                res.put("token", username);
//                response.getWriter().write(String.valueOf(res));
                flag = true;
                break;
            }

            if (flag == false) {
                JSONObject res = new JSONObject();
                res.put("username", "-1");
                response.getWriter().write(String.valueOf(res));
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
        if (flag) {

            try {
                connection = DBUtil.getConnection();

                // 构建查询语句
                String query = "insert into log (user_id, content, time) values (?, ?, ?)";
                statement = connection.prepareStatement(query);
                statement.setInt(1, id);
                statement.setString(2, "login");
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = currentTime.format(formatter);
                statement.setString(3, formattedTime);

                // 执行查询
                statement.executeUpdate();


                if (flag == false) {
                    JSONObject res = new JSONObject();
                    res.put("username", "-1");
                    response.getWriter().write(String.valueOf(res));
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

            String jwt = generateJWT(username);
            response.setContentType("application/json");
            response.getWriter().write("{\"token\": \"" + jwt + "\", \"userid\": " + id + "}");

        }


    }

    private String generateJWT(String username) {
        long currentTimeMillis = System.currentTimeMillis();
        Date issuedAt = new Date(currentTimeMillis);
        Date expiration = new Date(currentTimeMillis + 86400000); // 设置过期时间为 24 小时

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}