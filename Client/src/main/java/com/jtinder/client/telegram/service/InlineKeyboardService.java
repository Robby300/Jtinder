package com.jtinder.client.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InlineKeyboardService {
    public InlineKeyboardMarkup getInlineKeyboardSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton male = makeButton("Сударь", "MALE");
        InlineKeyboardButton female = makeButton("Сударыня", "FEMALE");

        List<InlineKeyboardButton> keyboardButtonsRow = makeKeyboardButtonsRow(male, female);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
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

    private List<InlineKeyboardButton> makeKeyboardButtonsRow(InlineKeyboardButton... buttons) {
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        Collections.addAll(keyboardButtonsRow, buttons);
        return keyboardButtonsRow;
    }

}
