package top.by.xs.controller;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.by.xs.vo.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    // produces = "application/json;charset=utf-8
    // 用来解决 return 的数据中中文乱码问题
    @RequestMapping(value = "doLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String doLogin(User user) {

        Subject subject = null;

        try {
            subject = this.login(user);
        } catch (Exception e) {
            return e.getMessage();
        }

        // 通过编程的方式授权
        if (subject.hasRole("admin")) {
            System.out.println("拥有角色：admin");
        }

        return "login success [登录认证成]";
    }

    /**
     * 主题具备角色才能访问
     *
     * @return
     */
    @RequiresRoles("admin")
    @RequestMapping(value = "/testRole", method = RequestMethod.GET)
    @ResponseBody
    public String testRole() {
        return "testRole success";
    }

    /**
     * 主题具备权限才能访问
     *
     * @return
     */
    @RequiresPermissions("user:delete")
    @RequestMapping(value = "/testPermissions", method = RequestMethod.GET)
    @ResponseBody
    public String testPermissions() {
        return "testPermissions success";
    }

    /**
     * 主题具备角色才能访问
     *
     * @return
     */
    @RequiresRoles("admin1")
    @RequestMapping(value = "/testRole1", method = RequestMethod.GET)
    @ResponseBody
    public String testRole1() {
        return "testRole1 success";
    }

    // 内置过滤器的使用
    @RequestMapping(value = "/testRole2", method = RequestMethod.GET)
    @ResponseBody
    public String testRole2() {
        return "testRole2 success";
    }

    @RequestMapping(value = "/testRole3", method = RequestMethod.GET)
    @ResponseBody
    public String testRole3() {
        return "testRole3 success";
    }

    @RequestMapping(value = "/testPerms", method = RequestMethod.GET)
    @ResponseBody
    public String testPerms() {
        return "testPerms success";
    }

    // ==========================================================================

    // http://localhost:8080/checkRoles?username=mark&password=123456&roles=admin&roles=user
    // user  = {username = mark, password = 123456}
    // roles = {admin, user}
    @RequestMapping(value = "/checkRoles", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public Object getRoles(User user, String ... roles) {
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            Subject subject = this.login(user);
            subject.hasRoles(Arrays.asList(roles));
            map.put("subject", subject);
        } catch (Exception e) {
            return e.getMessage();
        }

        return map;
    }

    private Subject login(User user) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        subject.login(token);
        return subject;
    }

}
