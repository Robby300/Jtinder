package com.jtinder.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PrerevolutionaryTinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrerevolutionaryTinderApplication.class, args);
    }

}
