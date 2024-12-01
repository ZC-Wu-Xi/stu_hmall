package com.hmall.common.interceptors;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZC_Wu 汐
 * @date 2024/11/30 14:36
 * @description 通用登录拦截器
 * 用户信息传递的拦截器 创建这个拦截器不会生效，需要一个配置类 我们写在了MvcConfig类中
 * 不需要做登录校验 网关中已经做了 这里的都登陆过了 这里只是获取用户信息做用户信息传递
 */
public class UserInfoInterceptor implements HandlerInterceptor {
    /**
     * controller 之前执行
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取登录用户信息
        String userInfo = request.getHeader("user-info");

        // 2. 判断是否读取了用户，如果有，存入ThreadLocal
        if (StrUtil.isNotBlank(userInfo)) { // 不为空
            UserContext.setUser(Long.valueOf(userInfo));
        }

        // 3. 放行
        return true;
    }

    /**
     * 所有业务执行完后清理用户
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
