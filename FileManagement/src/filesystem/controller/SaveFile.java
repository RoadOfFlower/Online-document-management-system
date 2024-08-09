package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import filesystem.util.DBUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "SaveFile", value = "/SaveFile")
public class SaveFile extends HttpServlet {
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
        String content = jsonNode.get("content").asText();
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
        int userid = jsonNode.get("userid").asInt();
        String uploadPath = Paths.get(path).toString();
        String uploadDirectory = getServletContext().getRealPath("/uploads/" + userid);
        String uploadPath_ = Paths.get(uploadDirectory, path).toString();
        response.setContentType("text/plain");
        if (uploadPath_ != null && !uploadPath_.isEmpty()) {
            if (saveFileContent(uploadPath_, content)) {
                res.put("code", 200);
                response.getWriter().write(String.valueOf(res));
                // response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int id = 0;

        try {
            connection = DBUtil.getConnection();

            // 构建查询语句
            String query = "SELECT * FROM user WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);

            // 执行查询
            resultSet = statement.executeQuery();
            // 处理查询结果
            while (resultSet.next()) {
                id = resultSet.getInt("id");
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

            // 构建查询语句
            String query = "insert into log (user_id, content, time) values (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.setString(2, "Modify file " + uploadPath);
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);
            statement.setString(3, formattedTime);

            // 执行查询
            statement.executeUpdate();

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

    private String readFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                return content.toString();
            }
        }
        return "";
    }

    private boolean saveFileContent(String filePath, String fileContent) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(fileContent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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