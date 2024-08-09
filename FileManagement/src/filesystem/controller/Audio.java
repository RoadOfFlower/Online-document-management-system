package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import filesystem.util.DBUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Audio", value = "/Audio")
public class Audio extends HttpServlet {
    private String message;

    private static final String SECRET_KEY = "123123";

    public void init() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = null;
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

        String path = jsonNode.get("path").asText();
        String uploadDirectory = getServletContext().getRealPath("/uploads");
        Path uploadPath_ = Paths.get(uploadDirectory, path);
        response.setContentType("audio/mpeg");
        // 获取响应的输出流
        try (FileInputStream fis = new FileInputStream(new File(uploadPath_.toString()));
             ServletOutputStream outputStream = response.getOutputStream()) {

            // 读取文件内容并写入输出流
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

    }

    private Map<String, Object> buildFileTree(List<String> filePathList) {
        Map<String, Object> fileTree = new HashMap<>();
        for (String filePath : filePathList) {
            String[] directories = filePath.split("/");
            String fileName = directories[directories.length - 1];

            Map<String, Object> currentLevel = fileTree;
            for (int i = 0; i < directories.length - 1; i++) {
                String directory = directories[i];
                if (!currentLevel.containsKey(directory)) {
                    currentLevel.put(directory, new HashMap<String, Object>());
                }
                currentLevel = (Map<String, Object>) currentLevel.get(directory);
            }

            currentLevel.put(fileName, null);
        }
        return fileTree;
    }

    private String convertToFileTreeJson(Map<String, Object> fileTree) {
        // 将文件树转换为 JSON 字符串的逻辑
        // 这里只是示例代码
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int count = 0;
        for (Map.Entry<String, Object> entry : fileTree.entrySet()) {
            if (count > 0) {
                sb.append(",");
            }
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() == null) {
                sb.append("null");
            } else {
                sb.append(convertToFileTreeJson((Map<String, Object>) entry.getValue()));
            }
            count++;
        }
        sb.append("}");
        return sb.toString();
    }
}