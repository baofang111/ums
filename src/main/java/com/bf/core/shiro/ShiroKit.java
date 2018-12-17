package com.bf.core.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;

import java.util.Random;

/**
 * shiro 工具类
 * Created by bf on 2017/8/5.
 */
public class ShiroKit {

    private static final String NAMES_DELIMETER = ",";

    /** 加盐参数*/
    public final static String HASHMD5SLAT = "MD5";

    /** 循环次数*/
    public final static int HASHINTERATIONS = 1024;


    /**
     * shiro MD5 加盐加密工具类
     * @param password
     * @param slat
     * @return
     */
    public static String md5(String password, String slat){
        ByteSource md5Hash = new Md5Hash(slat);
        return new SimpleHash(HASHMD5SLAT, password, md5Hash, HASHINTERATIONS).toString();
    }

    /**
     * 获取随机盐值 -- 保存用户信息的时候，保存一个随机 盐
     * @param length
     * @return
     */
    public static String getRandomSalt(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     *  获取 subject 对象
     */
    public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    /**
     *  获取封装的 ShiroUser 对象
     */
    public static ShiroUser getShiroUser(){
        if (isGuest()) {
            return null;
        } else {
            return (ShiroUser) getSubject().getPrincipals().getPrimaryPrincipal();
        }
    }

    /**
     *  获取 shiro 的 session 对象
     */
    public static Session getSession(){
        return getSubject().getSession();
    }

    /**
     * 获取shiro指定的sessionKey
     *
     */
/*    public static <T> T getSessionAttr(final String key) {
        Session session = getSession();
        return session != null ? (T) session.getAttribute(key) : null;
    }*/

    /**
     * 设定 shiro 中指定 sessionKey 的值
     */
/*
    public static void setSessionAttr(final String key, final Object value){
        Session session = getSession();
        session.setAttribute(key, value);
    }
*/

    /**
     * 判断当前用户是否拥有该角色
     * @param name -- 角色名
     */
    public static boolean hashRole(String name){
        return getSubject() != null && name != null && name.length() > 0 && getSubject().hasRole(name);
    }

    /**
     * 和 hashRole 逻辑想法，判断没有该角色
     * @param name
     * @return
     */
    public static boolean lacksRole(String name){
        return !getSubject().hasRole(name);
    }

    /**
     * 验证当前用户是否属于以下任意一个角色。
     *
     * @param roleNames
     *            角色列表
     * @return 属于:true,否则false
     */
    public static boolean hasAnyRoles(String roleNames) {
        boolean hasAnyRole = false;
        Subject subject = getSubject();
        if (subject != null && roleNames != null && roleNames.length() > 0) {
            for (String role : roleNames.split(NAMES_DELIMETER)) {
                if (subject.hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }
        return hasAnyRole;
    }

    /**
     * 验证当前用户是否属于以下所有角色。
     *
     * @param roleNames
     *            角色列表
     * @return 属于:true,否则false
     */
    public static boolean hasAllRoles(String roleNames) {
        boolean hasAllRole = true;
        Subject subject = getSubject();
        if (subject != null && roleNames != null && roleNames.length() > 0) {
            for (String role : roleNames.split(NAMES_DELIMETER)) {
                if (!subject.hasRole(role.trim())) {
                    hasAllRole = false;
                    break;
                }
            }
        }
        return hasAllRole;
    }

    /**
     * 验证当前用户是否拥有指定权限,使用时与lacksPermission 搭配使用
     *
     * @param permission
     *            权限名
     * @return 拥有权限：true，否则false
     */
    public static boolean hasPermission(String permission) {
        return getSubject() != null && permission != null && permission.length() > 0  && getSubject().isPermitted(permission);
    }

    /**
     * 与hasPermission标签逻辑相反，当前用户没有制定权限时，验证通过。
     *
     * @param permission
     *            权限名
     * @return 拥有权限：true，否则false
     */
    public static boolean lacksPermission(String permission) {
        return !hasPermission(permission);
    }

    /**
     * 已认证通过的用户。不包含已记住的用户，这是与user标签的区别所在。与notAuthenticated搭配使用
     *
     * @return 通过身份验证：true，否则false
     */
    public static boolean isAuthenticated() {
        return getSubject() != null && getSubject().isAuthenticated();
    }

    /**
     * 未认证通过用户，与authenticated标签相对应。与guest标签的区别是，该标签包含已记住用户。。
     *
     * @return 没有通过身份验证：true，否则false
     */
    public static boolean notAuthenticated() {
        return !isAuthenticated();
    }

    /**
     * 认证通过或已记住的用户。与guset搭配使用。getPrincipal() 获取身份对象
     *
     * @return 用户：true，否则 false
     */
    public static boolean isUser() {
        return getSubject() != null && getSubject().getPrincipal() != null;
    }

    /**
     * 验证当前用户是否为“访客”，即未认证（包含未记住）的用户。用user搭配使用
     * 未登录的情况下 getSubject().getPrincipal() 为null
     *
     * @return 访客：true，否则false
     */
    public static boolean isGuest() {
        return !isUser();
    }

    /**
     * 输出当前用户信息，通常为登录帐号信息。
     *
     * @return 当前用户信息
     */
    public static String principal() {
        if (getSubject() != null) {
            Object principal = getSubject().getPrincipal();
            return principal.toString();
        }
        return "";
    }







}
