package top.by.xs.relam;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.CollectionUtils;
import top.by.xs.dao.UserDao;
import top.by.xs.vo.User;

import javax.annotation.Resource;
import java.util.*;

/**
 * 自定义Realm的实现
 *
 * @author zwp
 */
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserDao userDao;

    // 盐
    public static final String SLAT = "md5";

//    Map<String, String> userMap = new HashMap<String, String>();
//
//    {
//        // userMap.put("mark", "123456");
//        // 123456 => MD5:e10adc3949ba59abbe56e057f20f883e
//        // userMap.put("mark", "e10adc3949ba59abbe56e057f20f883e");
//        // 123456 => slat MD5:ae8176b4caaf5f39283361ae3eacc71f
//        userMap.put("mark", "ae8176b4caaf5f39283361ae3eacc71f");
//        super.setName("customRealm");
//    }

    /**
     * 授权
     * @param principals 认证信息
     * @return
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        String userName = (String) principals.getPrimaryPrincipal();

        // 从数据库或者缓存中获取角色数据
        Set<String> roles = getRolesByUserName(userName);

        // 从数据库或者缓存中获取权限数据
        Set<String> permissions = getPermissionsByUserName(userName);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        // 设置角色
        authorizationInfo.setRoles(roles);

        // 设置权限
        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }

    /**
     * 认证
     *
     * @param token 主体传过来的认证的信息
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 1.从主体传过来的认证信息中获得用户名
        String userName = (String) token.getPrincipal();

        // 2.通过用户名到数据库中获取凭证
        String password = getPasswordByUserName(userName);

        if (password == null) {
            return null;
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userName, password, "customRealm"
        );

        // 加盐之后的操作
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(SLAT));

        return authenticationInfo;
    }

    /**
     * 获取密码
     *
     * @param userName
     * @return
     */
    private String getPasswordByUserName(String userName) {
        User user = userDao.getPasswordByUserName(userName);

        if (user == null) {
            return null;
        }

        return user.getPassword();
    }

    /**
     * 获取角色
     *
     * @param userName
     * @return
     */
    private Set<String> getRolesByUserName(String userName) {
        List<String> list = userDao.getRolesByUserName(userName);
        System.out.println("get db roles => " + list);
        Set<String> roles = new HashSet<String>(list);
        return roles;
    }

    /**
     * 获取权限
     *
     * @param userName
     * @return
     */
    private Set<String> getPermissionsByUserName(String userName) {
        List<String> list = userDao.getPermissionsByUserName(userName);
        Set<String> permissions = new HashSet<String>(list);
        return permissions;
    }

    public static void main(String[] args) {
        // 加盐
        Md5Hash md5Hash = new Md5Hash("123456", SLAT);
        System.out.println(md5Hash.toString());
    }
}
