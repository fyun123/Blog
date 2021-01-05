package com.whut.blog.dao;

import com.whut.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserNameAndPassword(String username, String password);
}
