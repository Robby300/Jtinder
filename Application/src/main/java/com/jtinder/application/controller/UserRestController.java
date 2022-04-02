package com.jtinder.application.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jtinder.application.domen.User;
import com.jtinder.application.domen.Views;
import com.jtinder.application.service.ImageService;
import com.jtinder.application.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;


@RequestMapping("users")
@RestController
public class UserRestController {
    private final UserService userService;
    private final ImageService imageService;

    public UserRestController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    @GetMapping()
    @JsonView(Views.Anketa.class)
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/search")
    @JsonView(Views.Anketa.class)
    public List<User> searchUsers() {
        return userService.searchUsers();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable(value = "id") User user) {
        return user;
    }

    @GetMapping("/exists/{id}")
    public boolean isUserExistById(@PathVariable(value = "id") Long userId) {
        return userService.isExists(userId);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable("id") User userFromDb,
                       @RequestBody User user) {
        BeanUtils.copyProperties(user, userFromDb, "id");
        return userService.save(userFromDb);
    }

    @PostMapping()
    @JsonView(Views.Anketa.class)
    public User saveUser(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/unlike/{user}")
    @JsonView(Views.Anketa.class)
    public User setUnLike(@PathVariable User user)  {
        userService.unlike(user);
        return user;
    }

    @PutMapping("/like/{user}")
    @JsonView(Views.Anketa.class)
    public User setLike(@PathVariable User user) {
        userService.like(user);
        return user;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(value = "id") User user) {
        userService.deleteUser(user);
    }

    // Временные методы:

    @GetMapping("/reciprocity/{id}")
    public boolean isReciprocity(@PathVariable(value = "id") User user) {
        return userService.isReciprocity(user);
    }

    @GetMapping("/welike")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllWeLike() {
        return userService.findAllWeLike();
    }

    @GetMapping("/uslike")
    @JsonView(Views.Anketa.class)
    public Set<User> findAllUsLike() {
        return userService.findAllUsLike();
    }

    @GetMapping("/male")
    @JsonView(Views.Anketa.class)
    public List<User> findAllMale() {
        return userService.findAllMale();
    }

    @GetMapping("/female")
    @JsonView(Views.Anketa.class)
    public List<User> findAllFemale() {
        return userService.findAllFemale();
    }

    @GetMapping("/image/{id}")
    public void getImage(@PathVariable(value = "id") User user) {
        try {
            imageService.getFile(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
