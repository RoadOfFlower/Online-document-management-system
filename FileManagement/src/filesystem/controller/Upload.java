package filesystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import filesystem.util.DBUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@WebServlet(name = "Upload", value = "/Upload")
@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 20971520, fileSizeThreshold = 5242880)
public class Upload extends HttpServlet {
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


        String jwt = request.getParameter("jwt");
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

        int userid = 0;

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
                userid = resultSet.getInt("id");
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

        Path uploadPath = null;
        Path uploadPath_ = null;
        Path uploadPath0 = null;

        try {
            // 获取上传的文件和路径参数
            Part filePart = request.getPart("file");
            String path = request.getParameter("path");


            // 从文件名中提取文件名
            String fileName = getFileName(filePart);

            // 确定文件在服务器上的保存路径
            // Path uploadPath = Paths.get("" + userid, path, fileName);
            String uploadDirectory = getServletContext().getRealPath("/uploads");
            uploadPath = Paths.get(String.valueOf(userid), path, fileName);
            uploadPath_ = Paths.get(uploadDirectory, String.valueOf(userid), path, fileName);
            uploadPath0 = Paths.get(path, fileName);


            // 判断目录是否存在，如果不存在则创建
            Path directory = uploadPath_.getParent();
            boolean ok = false;
            if (!Files.exists(directory)) {
                try {
                    Files.createDirectories(directory);
                    System.out.println("目录已创建：" + directory);
                } catch (IOException e) {
                    System.err.println("无法创建目录：" + directory);
                    e.printStackTrace();
                }
            }


            // 将上传的文件保存到服务器
            saveFile(filePart, uploadPath_);

            // 向数据库中插入记录
            PreparedStatement deleteStatement = null;
            PreparedStatement insertStatement = null;

            if (fileName.equals("file")) {
                fileName = "-";
                uploadPath = uploadPath.getParent();
            }
            try {
                // 创建数据库连接
                connection = DBUtil.getConnection();

                // 开启事务
                connection.setAutoCommit(false);

                // 查询是否存在匹配的记录
                String checkQuery = "SELECT COUNT(*) FROM file WHERE user_id = ? AND filename = ? AND filepath = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setInt(1, userid);
                checkStatement.setString(2, fileName);
                checkStatement.setString(3, uploadPath0.getParent().toString() + "/" + fileName);
                int count = 0;

                // 执行查询
                ResultSet resultSet2 = checkStatement.executeQuery();
                if (resultSet2.next()) {
                    count = 1;
                }

                // 关闭查询相关资源
                resultSet2.close();
                checkStatement.close();

                // 如果存在匹配的记录，先删除旧记录
                if (count > 0) {
                    String deleteQuery = "DELETE FROM file WHERE user_id = ? AND filename = ? AND filepath = ?";
                    deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, userid);
                    deleteStatement.setString(2, fileName);
                    deleteStatement.setString(3, uploadPath0.getParent().toString() + "/" + fileName);
                    deleteStatement.executeUpdate();
                }

                // 插入新数据
                String insertQuery = "INSERT INTO file (user_id, filename, filepath) VALUES (?, ?, ?)";
                insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, userid);
                insertStatement.setString(2, fileName);
                insertStatement.setString(3, uploadPath0.getParent().toString() + "/" + fileName);
                insertStatement.executeUpdate();

                // 提交事务
                connection.commit();

            } catch (SQLException e) {
                // 发生异常时回滚事务
                try {
                    if (connection != null) {
                        connection.rollback();
                    }
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
                e.printStackTrace();
            } finally {
                // 关闭数据库连接和相关资源
                try {
                    if (insertStatement != null) {
                        insertStatement.close();
                    }
                    if (deleteStatement != null) {
                        deleteStatement.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException closeException) {
                    closeException.printStackTrace();
                }
            }

            // 返回成功的响应;
//            res.put("code", 200);
//            response.getWriter().write(String.valueOf(res));
        } catch (Exception e) {
            // 处理上传失败的情况
            res.put("code", 500);
            response.getWriter().write(String.valueOf(res));
        }

        int id = 0;

        try {
            connection = DBUtil.getConnection();

            // 构建查询语句
            String query = "SELECT * FROM user WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username_);

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
            statement.setString(2, "Upload file " + uploadPath);
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

        res.put("code", 200);
        response.getWriter().write(String.valueOf(res));


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

    private String getFileName(Part filePart) {
        String header = filePart.getHeader("content-disposition");
        String fileName = header.substring(header.lastIndexOf("=") + 1).trim().replace("\"", "");
        return fileName;
    }

    private void saveFile(Part filePart, Path uploadPath) throws IOException {
        try (InputStream inputStream = filePart.getInputStream()) {
            Files.copy(inputStream, uploadPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}