package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.AuthenticUser;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.DataCache;
import com.jtinder.client.telegram.service.BotMethodService;
import com.jtinder.client.telegram.service.KeyboardService;
import com.jtinder.client.telegram.service.ServerService;
import com.jtinder.client.telegram.service.TextMessagesService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.List;

/**
 * Отвечает за вход уже зарегистрированного пользователя в систему
 */
@Component
@AllArgsConstructor
@Slf4j
public class LoginHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final BotMethodService botMethodService;


    /**
     * Формирует ответ в зависимости от полученного запроса.
     * Получает chatId пользователя и делает для него запрос на сервер,
     * для получения токена.
     *
     * @param message сообщение полученное из Update оступившего из от бота.
     * @return возвращает готовый ответ, в случае неверного запроса возвращает пустой List
     */
    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        long chatId = message.getChatId();

        User user = userDataCache.getUserProfileData(chatId);
        user.setToken(serverService.loginUser(new AuthenticUser(chatId, message.getText())));

        if (user.getToken().isEmpty()) {
            return List.of(botMethodService.getSendMessage(chatId,
                    messagesService.getText("reply.wrongPassword"),
                    keyboardService.getAuthenticateKeyboard()));
        }

        user.setProfile(serverService.getLoginUserProfile(user));
        userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);
        return Collections.singletonList(botMethodService.getSendMessage(
                chatId,
                messagesService.getText("reply.menu"),
                keyboardService.getMainKeyboard()));
    }

    /**
     * @return - возвращает состояние бота во время процедуры входа, для выбора соответствующего
     * обработчика в BotStateContext
     */
    @Override
    public BotState getHandlerName() {
        return BotState.LOGIN;
    }

    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        return Collections.emptyList();
    }
}
