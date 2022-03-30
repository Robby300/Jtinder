package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ServerService {
    RestTemplate restTemplate = new RestTemplate();

    static final String URL_USERS_JSON = "http://localhost:8080/users";

    public List<Profile> getUsersProfile() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbGV4ZXkiLCJleHAiOjE2NDg2NTI5MTEsImlhdCI6MTY0ODYzNDkxMX0.2sQ2j6IPpQvQtfB_kf_zTpVCPaKK3tp4UdHudwWeEo7TUMNrU7tDOeuXRHzZhVznwlpL9t4IzdyZaPx9GsgQxQ");
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(URL_USERS_JSON, HttpMethod.GET, requestEntity, Profile[].class);
        return List.of(usersResponse.getBody());
    }
}
