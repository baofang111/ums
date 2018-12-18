package com.bf.controller.global;

import com.bf.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 全局 controller
 * Created by bf on 2017/8/13.
 */
@Controller
@RequestMapping("/global")
public class GlobalController {

    /**
     * 跳转到404页面
     */
    @RequestMapping(path = "/error")
    public String errorPage() {

        //TODO 校验权限
        boolean aaa = ShiroKit.hashRole("超级管理员");
        boolean bbb = ShiroKit.hashRole("administrator");
        System.out.println(aaa);
        System.out.println(bbb);
        return "/404.html";
    }


    /**
     * 跳转到404页面
     */
    @RequestMapping(path = "/405")
    public String errorPage405() {
        //TODO 校验权限 OK
        boolean aaa = ShiroKit.hashRole("超级管理员");
        boolean bbb = ShiroKit.hashRole("administrator");
        System.out.println(aaa);
        System.out.println(bbb);

        return "/405.html";
    }

    /**
     * shiro session超时处理，超时返回登录页面
     */
    @RequestMapping(path = "/sessionError")
    public String sessionTimeout(Model model){
        model.addAttribute("tips","当前登录已过期，请重新登录哦，我们等你回来！！");
        return "/login.html";
    }


}
