package com.bf.controller;

import com.bf.common.entity.User;
import com.bf.common.node.MenuNode;
import com.bf.controller.global.BaseController;
import com.bf.core.shiro.ShiroKit;
import com.bf.core.shiro.ShiroUser;
import com.bf.core.shiro.factory.RedisShiroFactory;
import com.bf.core.util.ToolUtil;
import com.bf.dao.MenuMapper;
import com.bf.dao.UserMapper;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

/**
 * 登录
 * Created by bf on 2017/8/2.
 */
@Controller
@RequestMapping
public class LoginController extends BaseController {


    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     *  跳转首页
     * @param model
     * @return
     */
    @GetMapping(value = {"/"})
    public String index(Model model){
        // 获取当前登录用户的 角色List
        List<Integer> roleList = ShiroKit.getShiroUser().getRoleList();
        ShiroUser shiroUser = ShiroKit.getShiroUser();
        if(ToolUtil.isEmpty(roleList)){
            // 跳转到登录页面
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }
        /** 登录成功之后 重置登录限制的缓存*/
        RedisShiroFactory.me().resetLoginCount(shiroUser.getAccount());

        // 根据当前的角色 集合，获取这些角色的所有菜单节点
        List<MenuNode> menus = menuMapper.getMenusByRoleIds(roleList);
        List<MenuNode> menuNodes = MenuNode.buildTitle(menus);
        model.addAttribute("titles", menuNodes);

        // 获取用户头像
        Integer id = shiroUser.getId();
        User user = userMapper.selectByPrimaryKey(id);
        model.addAttribute("avatar", user.getAvatar());

        return "/index.html";
    }


    /**
     * get 方式跳转登录页面
     * @param model
     * @return
     */
    @GetMapping(value = {"/login"})
    public String login(Model model, String kickout){
        // 如果验证通过或者当前用户在 shiro 中有保存的对象信息，则为登录状态
        if(ShiroKit.isAuthenticated() || ShiroKit.getShiroUser() != null){
            return REDIRECT + "/";
        }
        if("1".equals(kickout)){
            // 被踢出登陆
            model.addAttribute("tips", "您的账号已在其他地方登陆");
        }
        return "/login.html";
    }

    /**
     *  post 接受登录参数，并登录
     * @return
     */
    @PostMapping(value = {"/login"})
    public String loginAjax(){
        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();

        // 获取登录对象 subject
        Subject subject = ShiroKit.getSubject();


        // 将用户登录的参数放入 token 中，可在 realm 中进行校验 默认设置为 记住我
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        // token.setRememberMe(true);
        // 登录，并将登录信息放入 subject
        subject.login(token);


        //登录后 获取 用户相信息，在下个页面进行显示
        ShiroUser shiroUser = ShiroKit.getShiroUser();
        super.getSession().setAttribute("shiroUser", shiroUser);
        super.getSession().setAttribute("username", shiroUser.getAccount());

        // 记录登录时候的session ，用于session超时的时候，提出当前登录的用户，session超时在 shiroConfgi 中的session管理中配置
        // ShiroKit.getSession().setAttribute("sessionSign",true);
        // 跳转首页
        return REDIRECT + "/";

    }


    @GetMapping(value = {"/logout"})
    public String logout(){
        ShiroKit.getSubject().logout();
        return REDIRECT + "/login";
    }



}
