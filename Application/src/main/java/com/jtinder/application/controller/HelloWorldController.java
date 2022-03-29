package com.jtinder.application.controller;

import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin()
public class HelloWorldController {

	@GetMapping({ "/hello" })
	public String hello() {
		return "Hello World";
	}

}
