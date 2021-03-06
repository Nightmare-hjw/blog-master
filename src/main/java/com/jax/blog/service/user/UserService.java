package com.jax.blog.service.user;

import com.jax.blog.model.User;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author huangjw
 * @Date 2018/9/5 16:29
 * @Version 1.0
 **/

public interface UserService {
    User getUserInfoById(Integer uid);

    User login(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException;

    int updateUserInfo(User user);
}
