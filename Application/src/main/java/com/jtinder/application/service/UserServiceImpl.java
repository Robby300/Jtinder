package com.jtinder.application.service;

import com.jtinder.application.domain.Sex;
import com.jtinder.application.domain.User;
import com.jtinder.application.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
        return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Отсутствует пользователь с id = " + userId));
    }

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

    @Override
    public List<User> searchUsers() {
        User currentUser = getCurrentUser();
        logger.info("Достыпные пользователи для текущего пользователя {}", currentUser.getName());
        Long userId = currentUser.getUserId();
        String sex = currentUser.getSex().toString();
        return userRepository.findAvailableUsersForCurrent(userId, sex);
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

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public String getImageDescription(User user) {
        String resultString = user.getSex().getName() + ", " + user.getName();
        User currentUser = getCurrentUser();
        if (isReciprocity(user)) {
            return resultString + ", Взаимность";
        } else if (currentUser.getWeLike().contains(user)) {
            return resultString + ", Любим" + (user.getSex().equals(Sex.FEMALE) ? "a" : "") + " вами.";
        } else if (currentUser.getUsLike().contains(user)) {
            return resultString + ", Вы любимы";
        }
        return resultString + ".";
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

    public Set<User> findAllReciprocity() {
        logger.info("список взаимных лайков");
        User currentUser = getCurrentUser();
        Set<User> weLike = currentUser.getWeLike();
        Set<User> usLike = currentUser.getUsLike();
        weLike.retainAll(usLike);
        return weLike;
    }
}
