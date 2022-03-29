package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends  UserDetailsService {
    User getCurrentUser();
    User save(User user);
    User findUserByUserName(String name);
    List<User> findAll();
    void deleteUser(User user);
    void like(User user);
    void unlike(User user);
    Set<User> findAllWeLike();
    Set<User> findAllUsLike();
    String getCurrentUserName();
}
