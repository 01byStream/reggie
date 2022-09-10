package com.bys.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.bys.reggie.common.BaseContext;
import com.bys.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Administrator
 * @version 1.0
 * @description: 过滤非法登录
 * @date 2022/9/5 16:57
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1. 获取本次请求的URL
        String url = request.getRequestURI();
        //无条件放行的请求
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //2. 判断本次请求是否需要处理，不需要则放行
        if (check(urls, url)) {
            filterChain.doFilter(request, response);
            return;
        }
        //3. 判断登录状态，已登录则放行
        if (request.getSession().getAttribute("employee") != null) {
            //登录则在本地线程里面放入用户id
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        //4. 未登录则返回未登录结果，获取输出流，向前端输出数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * @description: 匹配路径，检查本次请求是否放行
     * @param urls 匹配路径列表
     * @param url 需要匹配的路径
     * @return boolean
     * @author Administrator
     * @date 2022/9/5 20:07
     */
    public boolean check(String[] urls, String url) {
        for (String u : urls) {
            if (PATH_MATCHER.match(u, url)) {
                return true; //已匹配
            }
        }
        return false; //都不匹配
    }
}
