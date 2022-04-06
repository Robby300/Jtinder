package com.jtinder.client.telegram.cache;

import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, User> usersProfileData = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        return usersBotStates.get(userId);
    }

    @Override
    public User getUserProfileData(long userId) {
        User user = usersProfileData.getOrDefault(userId, new User(userId));
        usersProfileData.put(userId, user);
        return user;
    }
}