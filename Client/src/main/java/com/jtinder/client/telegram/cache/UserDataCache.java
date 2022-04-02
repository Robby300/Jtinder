package com.jtinder.client.telegram.cache;

import com.jtinder.client.domen.User;
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
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.ASK_SEX;
        }

        return botState;
    }

    @Override
    public User getUserProfileData(long userId) {
        return usersProfileData.getOrDefault(userId, new User(userId));
    }

    @Override
    public void saveUserProfileData(long userId, User user) {
        usersProfileData.put(userId, user);
    }
}
