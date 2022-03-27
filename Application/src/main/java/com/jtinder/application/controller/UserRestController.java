package com.jtinder.application.controller;

import com.jtinder.application.domen.User;
import com.jtinder.application.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
}
