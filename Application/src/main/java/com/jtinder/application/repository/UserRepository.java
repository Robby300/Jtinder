package com.jtinder.application.repository;

import com.jtinder.application.domen.Sex;
import com.jtinder.application.domen.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    List<User> findUsersBySexIs(Sex sex);
    User findUsersByName(String name);
    //Set<User> findUsersByWeLikeContaining(User currentUser);
}
