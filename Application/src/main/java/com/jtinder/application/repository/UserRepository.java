package com.jtinder.application.repository;

import com.jtinder.application.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {


    List<User> findAll();

    @Query(value = "select * from jtinder_db.usr\n" +
            "where sex in (select find_sex from jtinder_db.find_sex where user_id = :userId)\n" +
            "  and user_id != :userId\n" +
            "  and user_id not in (select like_id from jtinder_db.user_relationships where user_id = :userId)\n" +
            "  and user_id in (select user_id from jtinder_db.find_sex where find_sex = :sex)", nativeQuery = true)
    List<User> findAvailableUsersForCurrent(Long userId, String sex);

}
