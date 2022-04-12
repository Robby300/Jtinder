package com.jtinder.client.telegram.service;

import com.jtinder.client.domain.AuthenticUser;
import com.jtinder.client.domain.Profile;
import com.jtinder.client.domain.Token;
import com.jtinder.client.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Служит для связи с сервером - получения необходимой информации (анкеты пользователей,
 * списки поиска, лайки и тд.
 */
@Service
public class ServerService {
    private static final Logger log = LoggerFactory.getLogger(ServerService.class);

    private final RestTemplate restTemplate;
    private final AuthorizationService authorizationService;
    private final PrerevolutionaryTranslator translator;

    @Value("${key.localPath}")
    private String localPath;

    static final String URL_USERS = "users/search";
    static final String URL_LIKE = "users/like/%d";
    static final String URL_UNLIKE = "users/unlike/%d";
    static final String URL_REGISTRATION = "registration";
    static final String URL_LOGIN = "login";
    static final String URL_HOME = "home";
    static final String URL_IS_REGISTERED = "users/exists/%d";
    static final String URL_LOWERS = "users/likers";
    static final String URL_CAPTION = "users/imgdescr/%d";
    static final String URL_LOVE = "users/islove/%d";
    static final String URL_CHANGE_CURRENT = "users/update_current";

    public ServerService(RestTemplate restTemplate, AuthorizationService authorizationService, PrerevolutionaryTranslator translator) {
        this.restTemplate = restTemplate;
        this.authorizationService = authorizationService;
        this.translator = translator;
    }


    public List<Profile> getValidProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(localPath + URL_USERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        log.info("Получение {} анкет в поиске для текущего пользователя", usersResponse.getBody().length);
        return List.of(usersResponse.getBody());
    }

    public void updateCurrentUser(Profile profile, User user) {
        HttpEntity<Void> authEntity = authorizationService.getAuthorizationHeader(user);
        HttpEntity<Profile> entity = new HttpEntity<>(profile, authEntity.getHeaders());
        log.info("Обновление текущего пользователя с id = {}", profile.getUserId());
        restTemplate.postForObject(localPath + URL_CHANGE_CURRENT, entity, Void.class);
    }

    public void unLikeProfile(Long profileId, User user) {
        restTemplate.put(String.format(localPath + URL_UNLIKE, profileId), authorizationService.getAuthorizationHeader(user), profileId, Long.class);
        log.info("Текущий пользователь id = {} ставит лайк пользоваетлю id ={}", user.getProfile().getUserId(), profileId);
    }

    public boolean isRegistered(Long userId) {
        log.info("Регистрации пользователя с id = {}", userId);
        String test = localPath;
        return restTemplate.getForObject(String.format(localPath + URL_IS_REGISTERED, userId), Boolean.class);
    }

    public void registerUser(Profile profile) {
        log.info("Регистрации пользователя с id = {}", profile.getUserId());
        restTemplate.postForObject(localPath + URL_REGISTRATION, profile, Profile.class);
    }

    public String loginUser(AuthenticUser authenticUser) {
        HttpEntity<AuthenticUser> entity = new HttpEntity<>(authenticUser);
        try {
            Token token = restTemplate.postForObject(localPath + URL_LOGIN, entity, Token.class);
            log.info("Авторизация пользователя = {}", authenticUser.getUsername());
            return token.getToken();
        } catch (HttpClientErrorException e) {
            log.error("Ошибка авторизации " + e);
            return "";
        }
    }

    public Profile getLoginUserProfile(User user) {
        log.info("Запрос текущего пользователя id = {}", user.getProfile().getUserId());
        return restTemplate.postForObject(localPath + URL_HOME, authorizationService.getAuthorizationHeader(user), Profile.class);
    }

    public void likeProfile(Long profileId, User user) {
        restTemplate.put(String.format(localPath + URL_LIKE, profileId), authorizationService.getAuthorizationHeader(user), profileId, Long.class);
        log.info("Текущий пользователь id = {} ставит лайк пользоваетлю id ={}", user.getProfile().getUserId(), profileId);
    }

    public List<Profile> getLowersProfilesToUser(User user) {
        ResponseEntity<Profile[]> usersResponse = restTemplate.exchange(localPath + URL_LOWERS, HttpMethod.GET, authorizationService.getAuthorizationHeader(user), Profile[].class);
        log.info("Получение всех анкет, у которых есть отношения с текущим пользователем id = {} в количестве = {}", user.getProfile().getUserId(), usersResponse.getBody().length);
        return List.of(usersResponse.getBody());
    }

    public String getCaption(Long userId, User user) {
        ResponseEntity<String> caption = restTemplate.exchange(String.format(localPath + URL_CAPTION, userId), HttpMethod.GET, authorizationService.getAuthorizationHeader(user), String.class);
        log.info("Получение описания к картинке профиля для пользователя id = {}", userId);
        return translator.translate(caption.getBody());
    }

    public boolean weLove(Long userId, User user) {
        ResponseEntity<Boolean> love = restTemplate.exchange(String.format(localPath + URL_LOVE, userId), HttpMethod.GET, authorizationService.getAuthorizationHeader(user), boolean.class);
        log.info("Получение popUp сообщения для пользователя id = {}", userId);
        return love.getBody();
    }
}
