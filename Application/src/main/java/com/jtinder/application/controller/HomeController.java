package com.jtinder.application.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jtinder.application.domen.User;
import com.jtinder.application.domen.Views;
import com.jtinder.application.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin()
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @JsonView(Views.Anketa.class)
    @PostMapping("/home")
    public User home() {
        return userService.getCurrentUser();
    }

}
