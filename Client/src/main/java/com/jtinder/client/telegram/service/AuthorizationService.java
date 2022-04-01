package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.cache.UserDataCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class AuthorizationService {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";

    public HttpEntity<Void> getAuthorizationHeader(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, TOKEN_PREFIX + user.getToken());
        return new HttpEntity<>(headers);
    }
}
