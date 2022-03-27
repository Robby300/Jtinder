package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import com.jtinder.application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }
}
