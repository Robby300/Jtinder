package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService {
    User save(User user);
    //User findUserByUserChatId(Long id);
    List<User> findAll();
    void deleteUser(User user);

    void like(User currentUser, User user);
    void unlike(User currentUser, User user);

    Set<User> findAllWeLike(User currentUser);

    Set<User> findAllUsLike(User currentUser);
}
