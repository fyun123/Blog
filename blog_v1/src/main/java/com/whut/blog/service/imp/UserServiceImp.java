package com.whut.blog.service.imp;

import com.whut.blog.dao.UserRepository;
import com.whut.blog.po.User;
import com.whut.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String name, String password) {
        User user = userRepository.findByUserNameAndPassword(name,password);
        return user;
    }
}
