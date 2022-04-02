package com.jtinder.application.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jtinder.application.domen.User;
import com.jtinder.application.domen.Views;
import com.jtinder.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin()
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @JsonView(Views.Anketa.class)
    @PostMapping("/home")
    public User home() {
        logger.info("Запрос /home и получения текущего пользователя.");
        return userService.getCurrentUser();
    }

}
