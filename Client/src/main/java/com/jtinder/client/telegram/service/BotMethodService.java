package com.jtinder.client.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.io.File;
@Service
public class BotMethodService {
    public SendMessage getSendMessage(Long chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = getSendMessage(chatId, text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = getSendMessage(chatId, text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage getSendMessage(Long chatId, String text) {
        return new SendMessage(String.valueOf(chatId), text);
    }

    public SendPhoto getSendPhoto(Long chatId, File file, InlineKeyboardMarkup keyboardMarkup) {
        SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId), new InputFile(file));
        sendPhoto.setReplyMarkup(keyboardMarkup);
        return sendPhoto;
    }

    public DeleteMessage getDeleteMessage(Long chatId, Integer messageId) {
        return new DeleteMessage(String.valueOf(chatId), messageId);
    }
}
