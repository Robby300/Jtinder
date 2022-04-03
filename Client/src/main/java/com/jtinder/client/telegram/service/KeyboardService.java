package com.jtinder.client.telegram.service;

import com.jtinder.client.telegram.botapi.BotState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeyboardService {
    public InlineKeyboardMarkup getInlineKeyboardSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton male = makeButton("Сударь", "MALE");
        InlineKeyboardButton female = makeButton("Сударыня", "FEMALE");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(male, female);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup mainMenu = new ReplyKeyboardMarkup();
        mainMenu.setSelective(true);
        mainMenu.setResizeKeyboard(true);
        mainMenu.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Поиск"));
        row.add(new KeyboardButton("Анкета"));
        row.add(new KeyboardButton("Любимцы"));
        keyboard.add(row);
        mainMenu.setKeyboard(keyboard);

        return mainMenu;
    }



    public InlineKeyboardMarkup getInlineKeyboardFindSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardSex();
        inlineKeyboardMarkup.getKeyboard().get(0).add(makeButton("Всех", "ALL"));
        return inlineKeyboardMarkup;
    }


    private InlineKeyboardButton makeButton(String nameButton, String dataButton) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(nameButton);
        button.setCallbackData(dataButton);
        return button;
    }

    private List<InlineKeyboardButton> makeInlineKeyboardButtonsRow(InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        Collections.addAll(keyboardButtonsRow, buttons);
        return keyboardButtonsRow;
    }

    public InlineKeyboardMarkup getInlineKeyboardSearch() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton next = makeButton("\u27a1\ufe0f", "Следующий");
        InlineKeyboardButton menu = makeButton("MENU", "MENU");
        InlineKeyboardButton like = makeButton("\u2764\ufe0f", "Лайк");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(next, menu, like);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getInlineKeyboardLowers() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton next = makeButton("\u2b05\ufe0f", "Предыдущий");
        InlineKeyboardButton menu = makeButton("MENU", "MENUL");
        InlineKeyboardButton like = makeButton("\u27a1\ufe0f", "Следующий");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(next, menu, like);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


    public ReplyKeyboardMarkup getAuthenticateKeyboard() {
        ReplyKeyboardMarkup authenticateKeyboard = new ReplyKeyboardMarkup();
        authenticateKeyboard.setSelective(true);
        authenticateKeyboard.setResizeKeyboard(true);
        authenticateKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Регистрація"));
        row.add(new KeyboardButton("Внити"));
        keyboard.add(row);
        authenticateKeyboard.setKeyboard(keyboard);

        return authenticateKeyboard;
    }

    public EditMessageMedia getEditMessageImage(Long chatId, CallbackQuery callbackQuery, File image) {
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setChatId(chatId.toString());
        editMessageMedia.setMessageId(callbackQuery.getMessage().getMessageId());
        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
        inputMediaPhoto.setMedia(image, "Photo");
        editMessageMedia.setMedia(inputMediaPhoto);
        return editMessageMedia;
    }

    public InlineKeyboardMarkup getInlineMainMenu() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton next = makeButton("ПОИСК", "ПОИСК");
        InlineKeyboardButton menu = makeButton("АНКЕТА", "АНКЕТА");
        InlineKeyboardButton like = makeButton("ЛЮБИМЦЫ", "ЛЮБИМЦЫ");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(next, menu, like);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }




}
