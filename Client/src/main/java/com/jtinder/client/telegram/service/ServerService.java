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
    static final String URL_LIKE = "http://localhost:8080/users/like/%d";
    static final String URL_REGISTRATION = "http://localhost:8080/registration";
    static final String URL_LOGIN = "http://localhost:8080/login";
    static final String URL_HOME = "http://localhost:8080/home";
    static final String URL_IS_REGISTERED = "http://localhost:8080/users/exists/%d";
    static final String URL_LOWERS = "http://localhost:8080/users/likers";
    static final String URL_CAPTION = "http://localhost:8080/users/imgdescr/%d";

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

    public void likeProfile(Long profileId, User user) {
        restTemplate.put(String.format(URL_LIKE, profileId),authorizationService.getAuthorizationHeader(user), profileId, Long.class);
    }

    public List<Profile> getLowersProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(URL_LOWERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        return List.of(usersResponse.getBody());
    }

    public String getCaption(Long userId, User user) {
        ResponseEntity<String> caption = restTemplate.exchange(String.format(URL_CAPTION, userId), HttpMethod.GET, authorizationService.getAuthorizationHeader(user), String.class);
        return caption.getBody();
    }
}
