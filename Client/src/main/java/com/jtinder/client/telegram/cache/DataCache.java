package com.jtinder.client.telegram.cache;


import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.botapi.handlers.UserProfileData;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    UserProfileData getUserProfileData(long userId);

    void saveUserProfileData(long userId, UserProfileData userProfileData);
}
