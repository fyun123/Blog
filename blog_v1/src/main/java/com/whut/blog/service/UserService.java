package com.whut.blog.service;

import com.whut.blog.po.User;

public interface UserService {
    User checkUser(String name,String password);
}
