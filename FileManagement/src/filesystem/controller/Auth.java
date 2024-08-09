package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "Auth", value = "/Auth")
public class Auth extends HttpServlet {
    private String message;

    private static final String SECRET_KEY = "123123";

    public void init() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username;
        JSONObject res = new JSONObject();
        System.out.println(1111111);
        String jwt = request.getHeader("Authorization");
        if (jwt != null && jwt.startsWith("Bearer ")) {
            String token = jwt.substring(7); // 去除前缀 "Bearer "
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();

                username = claims.getSubject();
                res.put("code", 200);
                res.put("username", username);
                response.getWriter().write(String.valueOf(res));
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username;
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

                username = claims.getSubject();
                res.put("code", 200);
                res.put("username", username);
                response.getWriter().write(String.valueOf(res));
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
    }
}