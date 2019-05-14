package top.by.xs.dao;

import top.by.xs.vo.User;

import java.util.List;

public interface UserDao {

    User getPasswordByUserName(String userName);

    List<String> getRolesByUserName(String userName);

    List<String> getPermissionsByUserName(String userName);

}
