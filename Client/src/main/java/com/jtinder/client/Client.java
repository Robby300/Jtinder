package com.jtinder.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Client {
    static final String URL_EMPLOYEES_JSON = "http://localhost:8080/users";

    public static void main(String[] args) {
        SpringApplication.run(Client.class, args);
    }

    RestTemplate restTemplate = new RestTemplate();

    // Send request with GET method and default Headers.
    String result = restTemplate.getForObject(URL_EMPLOYEES_JSON, String.class);
	int i = 0;
}
