package com.jtinder.application.service;

import com.jtinder.application.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

    User getCurrentUser();

    User save(User user);

    void like(User user);

    void unlike(User user);

    Set<User> findAllWeLike();

    Set<User> findAllUsLike();

    boolean isReciprocity(User user);

    boolean isExists(Long userId);

    Set<User> findAllReciprocity();

    List<User> searchUsers();

    void deleteUser(User user);
    //List<User> findAllFemale();
    //List<User> findAllMale();
    //String getCurrentUserName();
    //List<User> findAll();
}
