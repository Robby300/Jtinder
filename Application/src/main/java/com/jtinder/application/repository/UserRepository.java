package com.jtinder.application.repository;

import com.jtinder.application.domen.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);
}
