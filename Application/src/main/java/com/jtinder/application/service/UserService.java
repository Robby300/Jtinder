package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    User save(User user);
    User findById(Long id);
    List<User> findAll();
    void deleteUser(User user);
}
