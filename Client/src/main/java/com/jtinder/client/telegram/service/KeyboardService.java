package com.jtinder.client.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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

        InlineKeyboardButton male = makeButton("\u27a1\ufe0f", "Следующий");
        InlineKeyboardButton female = makeButton("\u2764\ufe0f", "Лайк");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(male, female);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }

    public EditMessageText getEditMessageText(Long chatId, CallbackQuery callbackQuery, String newText) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setText(newText);
        return editMessageText;
    }



}
