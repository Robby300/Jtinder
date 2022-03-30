package com.jtinder.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Client {
//    static final String URL_USERS_JSON = "http://localhost:8080/users";
//    static final String URL_CREATE_USER = "http://localhost:8080/users";
//
//    RestTemplate restTemplate = new RestTemplate();
//
//    // Send request with GET method and default Headers.
//    String result = restTemplate.getForObject(URL_USERS_JSON, String.class);
//
//
//    User userForPost = User.builder()
//            .name("Васян")
//            .description("Я парень неплохой, только ссуся и глухой")
//            .findSex(Sex.FEMALE)
//            .build();
//
//    // Send request with POST method.
//    User u = restTemplate.postForObject(URL_CREATE_USER, userForPost, User.class);
//    int i = 0;


    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }


}
