package com.jtinder.application.controller;

import com.jtinder.application.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin()
public class HelloController {
	private final UserService userService;

	public HelloController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping({ "/hello" })
	public String hello() {
		return "Hello " + userService.getCurrentUserName();
	}

}
