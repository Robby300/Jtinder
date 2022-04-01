package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.AuthenticUser;
import com.jtinder.client.domen.Profile;
import com.jtinder.client.domen.Token;
import com.jtinder.client.domen.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@AllArgsConstructor
public class ServerService {
    private final RestTemplate restTemplate;
    private final AuthorizationService authorizationService;

    static final String URL_USERS = "http://localhost:8080/users/search";
    static final String URL_REGISTRATION = "http://localhost:8080/registration";
    static final String URL_LOGIN = "http://localhost:8080/login";
    static final String URL_HOME = "http://localhost:8080/home";
    static final String URL_IS_REGISTERED = "http://localhost:8080/users/exists/%d";

    public List<Profile> getValidProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(URL_USERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        return List.of(usersResponse.getBody());
    }

    public boolean isRegistered(Long userId) {
        return restTemplate.getForObject(String.format(URL_IS_REGISTERED, userId), Boolean.class);
    }

    public void registerUser(Profile profile) {
        restTemplate.postForObject(URL_REGISTRATION, profile, Profile.class);
    }

    public String loginUser(AuthenticUser authenticUser) {
        HttpEntity<AuthenticUser> entity = new HttpEntity<>(authenticUser);
        try {
            Token token = restTemplate.postForObject(URL_LOGIN, entity, Token.class);
            return token.getToken();
        } catch (HttpClientErrorException e) {
            return "";
        }

    }

    public Profile getLoginUserProfile(User user) {
        return restTemplate.postForObject(URL_HOME, authorizationService.getAuthorizationHeader(user), Profile.class);
    }
}
