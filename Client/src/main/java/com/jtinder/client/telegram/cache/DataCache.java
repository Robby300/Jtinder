package com.jtinder.client.telegram.cache;


import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;

public interface DataCache {
    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

    User getUserProfileData(long userId);


}
