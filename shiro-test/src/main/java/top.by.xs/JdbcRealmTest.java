package top.by.xs;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * shiro 内置Realm JdbcRealmTest
 */
public class JdbcRealmTest {

    DruidDataSource dataSource = new DruidDataSource();

    @Before
    public void serDatasource() {
        dataSource.setUrl("jdbc:mysql://192.168.1.15:3306/xiceos-shiro");
        dataSource.setUsername("macao");
        dataSource.setPassword("macao");
    }

    /**
     * 认证的测试
     */
    @Test
    public void testAutherizer() {

        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        // 权限开关默认为false
        jdbcRealm.setPermissionsLookupEnabled(true);

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("mark", "123456");
        subject.login(token);

        System.out.println("认证：" + subject.isAuthenticated());

        // 授权
        subject.checkRole("admin");

        // 权限
        subject.checkPermission("user:delete");

    }

    // 由于不可能和shiro中的默认的数据表一致
    // 使用自己的SQL
    // 创建一张新的表
    // 用来做认证
    // create table x_users(id int(10) primary key, user_name varchar(32), pass_word varchar(32));
    // insert into  x_users values (1, 'zwp', '123456');
    @Test
    public void testExecuteCustomSQL() {

        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);

        // 注意写SQL的时候，需要和JdbcRealm中的SQL一致
        // 即：
//        /**
//         * The default query used to retrieve account data for the user.
//         */
//        protected static final String DEFAULT_AUTHENTICATION_QUERY = "select password from users where username = ?";
//
//        /**
//         * The default query used to retrieve account data for the user when {@link #saltStyle} is COLUMN.
//         */
//        protected static final String DEFAULT_SALTED_AUTHENTICATION_QUERY = "select password, password_salt from users where username = ?";
//
//        /**
//         * The default query used to retrieve the roles that apply to a user.
//         */
//        protected static final String DEFAULT_USER_ROLES_QUERY = "select role_name from user_roles where username = ?";
//
//        /**
//         * The default query used to retrieve permissions that apply to a particular role.
//         */
//        protected static final String DEFAULT_PERMISSIONS_QUERY = "select permission from roles_permissions where role_name = ?";
        String sql = "select pass_word from x_users where user_name = ?";
        // 设置认证的SQL
        jdbcRealm.setAuthenticationQuery(sql);

        // 角色和权限，和认证的SQL是类似的处理方式

        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);

        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("zwp", "123456");
        subject.login(token);

        System.out.println("认证：" + subject.isAuthenticated());

    }

}

// 在jdbcRealm中有默认的SQL、查询了默认的表

// 1.需要事先创建shiro对应的默认数据表
// roles_permissions
// users
// user_roles

/*
create table users (
 id int(10) primary key,
 username varchar(32),
 password varchar(32)
);

create table user_roles (
 id int(10) primary key,
 username varchar(32),
 role_name varchar(32)
 );

create table roles_permissions (
 id int(10) primary key,
 role_name varchar(32),
 permission varchar(255)
);
*/

// 2.插入数据
/*
 insert into users values (1, 'mark', '123456');
 insert into user_roles values(1, 'mark', 'admin');
 insert into roles_permissions values(1, 'admin', 'user:delete');
 */
