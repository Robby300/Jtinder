package com.jtinder.application.controller;

import com.jtinder.application.domen.User;
import com.jtinder.application.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RequestMapping("users")
@RestController
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable(value = "id") User user) {
        return user;
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") User userFromDb,
                       @RequestBody User user) {
        BeanUtils.copyProperties(user, userFromDb, "id");
        return userService.save(userFromDb);
    }

    @PostMapping()
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") User user) {
        userService.deleteUser(user);
    }

    // Временные костыльные методы:

    @PutMapping("/{id}/like/{like_id}")
    public User setLike(@PathVariable("id") User currentUser, @PathVariable("like_id") User user) {
        userService.like(currentUser, user);
        return currentUser;
    }

    @PutMapping("/{id}/unlike/{like_id}")
    public User setUnLike(@PathVariable("id") User currentUser, @PathVariable("like_id") User user) {
        userService.unlike(currentUser, user);
        return currentUser;
    }

    @GetMapping("/{id}/welike")
    public Set<User> findAllWeLike(@PathVariable("id") User currentUser) {
        return userService.findAllWeLike(currentUser);
    }

    @GetMapping("/{id}/uslike")
    public Set<User> findAllUsLike(@PathVariable("id") User currentUser) {
        return userService.findAllUsLike(currentUser);
    }
}
