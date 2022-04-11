package com.jtinder.client.telegram.handlers;

import com.jtinder.client.domain.AuthenticUser;
import com.jtinder.client.domain.Sex;
import com.jtinder.client.domain.User;
import com.jtinder.client.telegram.botapi.BotState;
import com.jtinder.client.telegram.cache.DataCache;
import com.jtinder.client.telegram.service.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Формирует анкету пользователя.
 */

@AllArgsConstructor
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final DataCache userDataCache;
    private final TextMessagesService messagesService;
    private final KeyboardService keyboardService;
    private final ServerService serverService;
    private final ImageService imageService;
    private final BotMethodService botMethodService;

    /**
     * Определяет, что пользователь находится в состоянии заполнения анкеты
     * и отправляет его на первый шаг, либо на последующий если он уже заполнил предыдущий
     */
    @Override
    public List<PartialBotApiMethod<?>> handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_NAME);
        }
        return processUsersInput(message);
    }

    /**
     * @return - возвращает состояние бота во время заполнения анкеты, для выбора соответствующего
     * обработчика в BotStateContext
     */
    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    /**
     * обрабатывает сообщения типа Message
     * Определяет на каком шаге заполнения находится пользователь, запрашивает
     * соответствующую информацию и устанавливает состояние на следующий шаг
     */
    private List<PartialBotApiMethod<?>> processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        long chatId = inputMsg.getChatId();

        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (botState.equals(BotState.ASK_SEX)) {
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_NAME);
            user.getProfile().setPassword(usersAnswer);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askSex"),
                    keyboardService.getInlineKeyboardSex()));
        }

        if (botState.equals(BotState.ASK_DESCRIPTION)) {
            user.getProfile().setName(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_FIND);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askDescription")));
        }

        if (botState.equals(BotState.ASK_FIND)) {
            user.getProfile().setDescription(usersAnswer);
            userDataCache.setUsersCurrentBotState(chatId, BotState.PROFILE_FILLED);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askFindSex"),
                    keyboardService.getInlineKeyboardFindSex()));
        }

        return Collections.emptyList();
    }

    /**
     * обрабатывает сообщения типа Message
     * Определяет на каком шаге заполнения находится пользователь, запрашивает
     * соответствующую информацию и устанавливает состояние на следующий шаг.
     * В случае успешной регистрации происходит вход в систему и присвоение токена.
     */
    @Override
    public List<PartialBotApiMethod<?>> handle(CallbackQuery callbackQuery) {
        String usersAnswer = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        User user = userDataCache.getUserProfileData(chatId);
        BotState botState = userDataCache.getUsersCurrentBotState(chatId);

        if (botState.equals(BotState.ASK_NAME)) {
            user.getProfile().setSex(Sex.valueOf(usersAnswer));
            userDataCache.setUsersCurrentBotState(chatId, BotState.ASK_DESCRIPTION);
            return Collections.singletonList(botMethodService.getSendMessage(
                    chatId,
                    messagesService.getText("reply.askName")));
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {
            user.getProfile().setFindSex(new HashSet<>());
            if (usersAnswer.equals(messagesService.getText("button.allSex"))) {
                user.getProfile().getFindSex().add(Sex.MALE);
                user.getProfile().getFindSex().add(Sex.FEMALE);
            } else {
                user.getProfile().getFindSex().add(Sex.valueOf(usersAnswer));
            }

            serverService.registerUser(user.getProfile());
            user.setToken(serverService.loginUser(new AuthenticUser(user.getProfile().getUserId(), user.getProfile().getPassword())));
            userDataCache.setUsersCurrentBotState(chatId, BotState.MAIN_MENU);


            return Collections.singletonList(botMethodService.getSendPhoto(chatId,
                    imageService.getFile(user.getProfile()),
                    keyboardService.getMainKeyboard(), user.getProfile().getSex().getName() + ", " +
                            user.getProfile().getName()));
        }
        return Collections.emptyList();
    }
}
