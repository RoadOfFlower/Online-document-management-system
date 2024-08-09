package filesystem.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter {

    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(true);
        String username = (String) session.getAttribute("username");//
        String url = request.getRequestURI();
        if (username == null || username.equals("")) {
            // 判断获取的路径不为空且不是访问登录页面或执行登录操作时跳转
            if (url != null && !url.equals("") && (url.indexOf("Login") < 0 && url.indexOf("login") < 0)) {
                response.sendRedirect("login.jsp");
                return;
            }
        }
        // 已通过验证，用户访问继续
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

}