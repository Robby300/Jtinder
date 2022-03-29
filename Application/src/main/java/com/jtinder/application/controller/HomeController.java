package com.jtinder.application.controller;

import com.jtinder.application.domen.User;
import com.jtinder.application.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin()
public class HomeController {
	private final UserService userService;

	public HomeController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping({ "/home" })
	public User home() {
		return userService.getCurrentUser();
	}

}
