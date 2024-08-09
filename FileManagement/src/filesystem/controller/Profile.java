package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import filesystem.util.DBUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@WebServlet(name = "Profile", value = "/Profile")
public class Profile extends HttpServlet {
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

        String username_ = null;

        JSONObject res = new JSONObject();
        System.out.println(1111111);
        // String jwt = request.getParameter("jwt");
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        // 使用 Jackson ObjectMapper 进行 JSON 解析
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody.toString());

        // 获取 JWT 值
        String jwt = jsonNode.get("jwt").asText();
        if (jwt != null && jwt.startsWith("Bearer ")) {
            String token = jwt.substring(7); // 去除前缀 "Bearer "
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                username_ = claims.getSubject();
            } catch (Exception e) {
                // JWT 验证失败，返回错误状态或重定向到登录页面
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.put("code", 405);
                response.getWriter().write(String.valueOf(res));
            }
        } else {
            // 未提供 JWT，返回错误状态或重定向到登录页面
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.put("code", 405);
            response.getWriter().write(String.valueOf(res));
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBUtil.getConnection();
            // 构建查询语句
            String query = "SELECT * FROM user WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username_);

            // 执行查询
            resultSet = statement.executeQuery();


            boolean flag = false;

            // 处理查询结果
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String tel = resultSet.getString("tel");

                // 在这里可以根据需要进行进一步的操作，例如打印数据或将数据添加到集合中等
                System.out.println("ID: " + id);
                System.out.println("Username: " + username_);
                System.out.println("Email: " + email);
                System.out.println("Tel: " + tel);
                res.put("username", username_);
                res.put("email", email);
                res.put("tel", tel);
                response.getWriter().write(String.valueOf(res));
//                request.getSession().setAttribute("userid", id);
//                request.getSession().setAttribute("username", username);
//                JSONObject res = new JSONObject();
//                res.put("username", username);
//                res.put("token", username);
//                response.getWriter().write(String.valueOf(res));
                flag = true;
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