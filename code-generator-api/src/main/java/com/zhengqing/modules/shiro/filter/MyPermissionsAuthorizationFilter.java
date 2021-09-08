package com.zhengqing.modules.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p> 自定义url过滤权限zqPerms  -  - 每次zqPerms拦截的请求都会进入此  </p>
 *
 * @author : zhengqing
 * @description :
 * @date : 2019/8/26 12:58
 */
@Slf4j
public class MyPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestUrl = httpRequest.getServletPath();
        log.info("请求的url:  " + requestUrl);
        // 检查是否拥有访问权限
        Subject subject = this.getSubject(request, response);
        if (subject.getPrincipal() == null) {
            this.saveRequestAndRedirectToLogin(request, response);
        } else {
            // 转换成http的请求和响应
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;

            // 获取请求头的值
            String header = req.getHeader("X-Requested-With");
            // ajax 的请求头里有X-Requested-With: XMLHttpRequest      正常请求没有
            if (header != null && "XMLHttpRequest".equals(header)) {
                resp.setContentType("text/json,charset=UTF-8");
                resp.getWriter().print("{\"success\":false,\"msg\":\"没有权限操作！\"}");
            } else {  //正常请求
                String unauthorizedUrl = this.getUnauthorizedUrl();
                if (StringUtils.hasText(unauthorizedUrl)) {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } else {
                    WebUtils.toHttp(response).sendError(401);
                }
            }

        }
        return false;
    }
}
