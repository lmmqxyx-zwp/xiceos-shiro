package top.by.xs.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 授权相关的继承：AuthorizationFilter
 * 认证相关的继承：AuthenticationFilter
 *
 * 实现的功能：
 *  在spring.xml配置的内置filter中有
 *     <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
 *         <property name="securityManager" ref="securityManager"/>
 *         <property name="loginUrl" value="login.html"/>
 *         <property name="unauthorizedUrl" value="403.html"/>
 *         <property name="filterChainDefinitions">
 *             <value>
 *                 /login.html = anon
 *                 /doLogin = anon
 *                 /testRole2 = roles["admin"]
 *                 /testRole3 = roles["admin", "user"]
 *                 /testPerms = perms["user:delete"]
 *                 /* = authc
 *             </value>
 *         </property>
 *     </bean>
 *
 * /testRole3 = roles["admin", "user"]：满足所有的角色才能进入
 *
 * 则：
 * 本filter实现或的功能
 *
 * @author zwp
 */
public class RolesOrFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        Subject subject = getSubject(request, response);

        String[] roles = (String[]) mappedValue;

        // 没有设置对于filter的角色
        if (roles == null || roles.length == 0) {
            return true;
        }

        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
            }
        }

        return false;
    }
}
