package com.jtinder.application.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jtinder.application.domen.User;
import com.jtinder.application.domen.Views;
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
    @JsonView(Views.Anketa.class)
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

    @PutMapping("/like/{user}")
    public User setLike(@PathVariable User user) {
        userService.like(user);
        return user;
    }

    @PutMapping("/unlike/{user}")
    public User setUnLike(@PathVariable User user)  {
        userService.unlike(user);
        return user;
    }

    @GetMapping("/welike")
    public Set<User> findAllWeLike() {
        return userService.findAllWeLike();
    }

    @GetMapping("/uslike")
    public Set<User> findAllUsLike() {
        return userService.findAllUsLike();
    }

    @GetMapping("/male")
    public List<User> findAllMale() {
        return userService.findAllMale();
    }

    @GetMapping("/female")
    public List<User> findAllFemale() {
        return userService.findAllFemale();
    }
}
