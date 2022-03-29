package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import com.jtinder.application.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


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
    public User findUserByUserChatId(Long id) {
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

    @Override
    public void like(User currentUser, User user) {
        currentUser.getWeLike().add(user);
        userRepository.save(currentUser);
    }

    @Override
    public void unlike(User currentUser, User user) {
        currentUser.getWeLike().remove(user);
        userRepository.save(currentUser);
    }

    @Override
    public Set<User> findAllWeLike(User currentUser) {
        return findUserByUserChatId(currentUser.getUserChatId()).getWeLike();
    }

    @Override
    public Set<User> findAllUsLike(User currentUser) {
        return findUserByUserChatId(currentUser.getUserChatId()).getUsLike();
    }
}
