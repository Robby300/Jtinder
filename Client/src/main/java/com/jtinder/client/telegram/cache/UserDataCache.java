package com.jtinder.client.telegram.cache;

import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new HashMap<>();
    private final Map<Long, User> usersProfileData = new HashMap<>();
    private final Map<Long, Set<Integer>> messagesToDelete = new HashMap<>();


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

    public void addMessage(Long chatId, Integer messageId) {
        Set<Integer> setMessage = messagesToDelete.getOrDefault(chatId, new HashSet<>());
        setMessage.add(messageId);
        messagesToDelete.put(chatId, setMessage);
    }

    public Set<Integer> getMessagesToDelete(Long chatId) {
        Set<Integer> setMessage = messagesToDelete.getOrDefault(chatId, new HashSet<>());
        messagesToDelete.put(chatId, new HashSet<>());
        return setMessage;
    }

    public void setMessagesToDelete(Long chatId, Set<Integer> messages) {
        messagesToDelete.put(chatId, messages);
    }
}
