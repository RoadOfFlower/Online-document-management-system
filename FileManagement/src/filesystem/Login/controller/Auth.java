package filesystem.Login.controller;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Auth", value = "/Auth")
public class Auth extends HttpServlet {
    private String message;

    public void init() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JSONObject res = new JSONObject();
        if (request.getSession().getAttribute("userid") == null) {
            res.put("code", 405);
            response.getWriter().write(String.valueOf(res));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}