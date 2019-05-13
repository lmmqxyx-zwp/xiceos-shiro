package top.by.xs;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * shiro 内置Realm IniRealm
 */
public class IniRealmTest {

    IniRealm iniRealm = new IniRealm("classpath:user.ini");

    @Test
    public void testAutherizer() {

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("mark", "123456");
        subject.login(token);

        System.out.println("认证：" + subject.isAuthenticated());

        // 授权
        subject.checkRole("admin");

        // 权限
        // 是否具备用删除的权限
        subject.checkPermission("user:delete");
        subject.checkPermission("user:select");

    }

}

// user.ini

// 1.认证
// [users]
// mark=123456

// 2.授权
// [users]
// mark=123456,admin
// [roles]
// admin=user:delete
// #注释：mark有admin的角色，有删除用户的权限

// 权限是否有增删改查 ?
// 改变user.ini
// [users]
// mark=123456,admin
// [roles]
// admin=user:delete,user:update
// 注释：和角色后面的东西直接匹配，即直接匹配user:update，如果是user:update1，则与user:update1匹配