package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domen.AuthenticUser;
import com.jtinder.client.domen.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.UserDataCache;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ReplyMessagesService;
import com.jtinder.client.telegram.service.ServerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class LoginHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;


    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        List<PartialBotApiMethod<?>> answerList = new ArrayList<>();
        long chatId = message.getChatId();

        User user = userDataCache.getUserProfileData(chatId);

        SendMessage replyToUser = new SendMessage();
        replyToUser.enableMarkdown(true);
        replyToUser.setReplyMarkup(keyboardService.getInlineMainMenu());
        replyToUser.setChatId(String.valueOf(chatId));
        user.setToken(serverService.loginUser(new AuthenticUser(chatId, message.getText())));
        user.setProfile(serverService.getLoginUserProfile(user));
        replyToUser.setText("МЕНЮ");
        answerList.add(new DeleteMessage(String.valueOf(chatId), message.getMessageId()));
        answerList.add(replyToUser);
        userDataCache.saveUserProfileData(chatId, user);
        userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        return answerList;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LOGIN;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return null;
    }
}
