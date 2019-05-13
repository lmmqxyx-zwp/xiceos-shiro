package top.by.xs;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import top.by.xs.cust.relam.CustomRealm;

public class CustomRealmTest {

    /**
     * 测试认证
     */
    @Test
    public void testDoGetAuthenticationInfo() {
        CustomRealm customRealm = new CustomRealm();

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        // 使用加密之后认证
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 1.设置加密算法的名称
        matcher.setHashAlgorithmName("md5");
        // 2.设置加密的次数：多次或者一次
        matcher.setHashIterations(1);

        // 在自定义realm中设置加密对象
        customRealm.setCredentialsMatcher(matcher);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("mark", "123456");
        subject.login(token);

        System.out.println("认证：" + subject.isAuthenticated());
    }

    /**
     * 测试授权
     */
    @Test
    public void testDoGetAuthorizationInfo() {
        CustomRealm customRealm = new CustomRealm();

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("mark", "123456");
        subject.login(token);

        System.out.println("认证：" + subject.isAuthenticated());

        subject.checkRole("admin");

        subject.checkPermission("admin:update");
    }


}
