package top.by.xs.controller;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import top.by.xs.vo.User;

@Controller
public class UserController {

    // produces = "application/json;charset=utf-8
    // 用来解决 return 的数据中中文乱码问题
    @RequestMapping(value = "doLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String doLogin(User user) {

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());

        try {
            subject.login(token);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return "login success [登录认证成]";
    }

}
