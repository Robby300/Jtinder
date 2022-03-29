package com.jtinder.application.repository;

import com.jtinder.application.domen.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    //Set<User> findUsersByWeLikeContaining(User currentUser);
}
