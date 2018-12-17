package com.bf.core.interceptor;

import com.bf.core.shiro.ShiroKit;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * session 超时拦截器，用于拦截session超时，然后踢出登录
 * Created by bf on 2017/8/14.
 */
public class SessionTimeOutInterceptor implements HandlerInterceptor {

    /**
     * 请求之前拦截
     */
    //TODO 有些不该拦截器的请求还是拦截了
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // 获取当前的请求URL ---> 是 getServletPath 不是 contentPath
/*        String contextPath = httpServletRequest.getServletPath();
        if(contextPath.contains("/login") || contextPath.contains("/global/sessionError") || contextPath.contains("/home")
                || contextPath.contains("/error") || contextPath.equals("/")){
            // / 当前项目路径也过滤
            return true;
        }else{
            // 校验当前session是否过期，过期的话，退出当前用户，并抛出异常
            if(ShiroKit.getSession().getAttribute("sessionSign") == null){
                ShiroKit.getSubject().logout();
                throw new InvalidSessionException();
            }
        }*/
        return true;
    }

    /**
     * 这个方法只有在  preHandle 方法 返回 true 的时候才执行
     * postHandle 是处理拦截请求的，且在请求之后进行，但是他在 DispatcherServlet 渲染视图之前进行，所以你可以在这个方法里面对 model 进行渲染
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 该方法也是当  preHandle 返回 ture 的时候才执行，且他的执行顺序是等到 DispatcherServlet 全部完成操作的时候才执行，这时候视图已经渲染完毕
     * 该方法主要的作用是用来清理资源的
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
