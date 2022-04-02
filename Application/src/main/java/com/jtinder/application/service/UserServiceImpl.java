package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import com.jtinder.application.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Long id = Long.parseLong(userId);
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Нет такого!"));
    }

    /*@Override
    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }*/

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Вызов текущего пользователя.");
        return (User) loadUserByUsername(auth.getName());
    }

    @Override
    public boolean isReciprocity(User user) {
        logger.info("Проверка взаимности.");
        return getCurrentUser().getWeLike().contains(user)
                && getCurrentUser().getUsLike().contains(user);
    }

    /*@Override
    public List<User> findAllMale() {
        return userRepository.findUsersBySexIs(Sex.MALE);
    }

    @Override
    public List<User> findAllFemale() {
        return userRepository.findUsersBySexIs(Sex.FEMALE);
    }*/

    @Override
    public List<User> searchUsers() {
        User currentUser = getCurrentUser();
        logger.info("Достыпные пользователи для текущего пользователя {}", currentUser.getName());
        return userRepository.findAvailableUsersForCurrent(currentUser.getUserId(), currentUser.getSex().toString());
    }

    @Override
    public boolean isExists(Long userId) {
        logger.info("Проверка, есть ли пользоваетль {}", userId);
        return userRepository.findById(userId).isPresent();
    }


    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

/*    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }*/

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void like(User user) {
        User currentUser = getCurrentUser();
        currentUser.getWeLike().add(user);
        logger.info("Пользователь {} ставит лайк пользователю {}", currentUser.getName(), user.getName());
        userRepository.save(currentUser);
    }

    @Override
    public void unlike(User user) {
        User currentUser = getCurrentUser();
        currentUser.getWeLike().remove(user);
        logger.info("Пользователь {} отбирает лайк у пользователю {}", currentUser.getName(), user.getName());
        userRepository.save(currentUser);
    }

    @Override
    public Set<User> findAllWeLike() {
        logger.info("Список кому текущий пользователь поставил лайк.");
        return getCurrentUser().getWeLike();
    }

    @Override
    public Set<User> findAllUsLike() {
        logger.info("Кто постаил лайк текущему пользователю.");
        return getCurrentUser().getUsLike();
    }
}
