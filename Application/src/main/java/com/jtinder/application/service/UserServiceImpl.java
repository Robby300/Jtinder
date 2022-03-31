package com.jtinder.application.service;

import com.jtinder.application.domen.Sex;
import com.jtinder.application.domen.User;
import com.jtinder.application.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Long id = Long.parseLong(userId);
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Нет такого!"));
    }

    @Override
    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    @Override
    public boolean isReciprocity(User user) {
        return getCurrentUser().getWeLike().contains(user)
                && getCurrentUser().getUsLike().contains(user);
    }

    @Override
    public List<User> findAllMale() {
        return userRepository.findUsersBySexIs(Sex.MALE);
    }

    @Override
    public List<User> findAllFemale() {
        return userRepository.findUsersBySexIs(Sex.FEMALE);
    }

    @Override
    public List<User> searchUsers() {
        User currentUser = getCurrentUser();
        Set<User> excludedUsers = currentUser.getWeLike();
        excludedUsers.add(currentUser);
        List<Long> collect = excludedUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
        return userRepository.findUsersBySexEqualsAndUserIdIsNotIn(Sex.FEMALE, collect); // заглушка
    }

    @Override
    public boolean isExists(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    @Override
    public User getCurrentUser() {
        return (User) loadUserByUsername(getCurrentUserName());
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
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
    public void like(User user) {
        User currentUser = getCurrentUser();
        currentUser.getWeLike().add(user);
        userRepository.save(currentUser);
    }

    @Override
    public void unlike(User user) {
        User currentUser = getCurrentUser();
        currentUser.getWeLike().remove(user);
        userRepository.save(currentUser);
    }

    @Override
    public Set<User> findAllWeLike() {
        return getCurrentUser().getWeLike();
    }

    @Override
    public Set<User> findAllUsLike() {
        return getCurrentUser().getUsLike();
    }
}
