package com.bf.core.aop;

import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局 异常控制器
 * Created by bf on 2017/8/5.
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 错误页面404 页面跳转
     */
/*    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/global/error");
        return mav;
    }*/

    /**
     *  账号异常 全局拦截器
     */
    @ExceptionHandler(CredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String acountError(CredentialsException e, Model model){
        model.addAttribute("tips", "账号密码错误");
        return "/login.html";
    }


    /**
     *  账号重试次数超过最大限制 全局拦截器
     */
    @ExceptionHandler(DisabledAccountException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String accountLock(DisabledAccountException e, Model model){
        model.addAttribute("tips", e.getMessage());
        return "/login.html";
    }

    /**
     *  session 超时 全局拦截器
     */
    @ExceptionHandler(InvalidSessionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String acountError(InvalidSessionException e, Model model){
        model.addAttribute("tips","当前登录已过期，请重新登录哦，我们等你回来！！");
        return "/login.html";
    }



}
