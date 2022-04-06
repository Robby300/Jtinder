package com.jtinder.client.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class KeyboardService {

    private  final TextMessagesService messagesService;

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

    public InlineKeyboardMarkup getInlineKeyboardEditSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton male = makeButton("Сударь", "MALEEDIT");
        InlineKeyboardButton female = makeButton("Сударыня", "FEMALEEDIT");
        InlineKeyboardButton all = makeButton("Всех", "ALLEDIT");

        List<InlineKeyboardButton> keyboardButtonsRow = makeInlineKeyboardButtonsRow(male, female, all);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }



    public InlineKeyboardMarkup getInlineKeyboardFindSex() {
        InlineKeyboardMarkup inlineKeyboardMarkup = getInlineKeyboardSex();
        inlineKeyboardMarkup.getKeyboard().get(0).add(makeButton(messagesService.getText("button.allSex"),
                messagesService.getText("button.alledit")));
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

    public ReplyKeyboardMarkup getKeyboardSearch() {
        ReplyKeyboardMarkup searchKeyboard = new ReplyKeyboardMarkup();
        searchKeyboard.setSelective(true);
        searchKeyboard.setResizeKeyboard(true);
        searchKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(messagesService.getText("button.next")));
        row.add(new KeyboardButton(messagesService.getText("button.menu")));
        row.add(new KeyboardButton(messagesService.getText("button.like")));
        keyboard.add(row);
        searchKeyboard.setKeyboard(keyboard);
        return searchKeyboard;
    }

    public ReplyKeyboardMarkup getKeyboardLowers() {
        ReplyKeyboardMarkup LoversKeyboard = new ReplyKeyboardMarkup();
        LoversKeyboard.setSelective(true);
        LoversKeyboard.setResizeKeyboard(true);
        LoversKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(messagesService.getText("button.prev")));
        row.add(new KeyboardButton(messagesService.getText("button.menu")));
        row.add(new KeyboardButton(messagesService.getText("button.next")));
        keyboard.add(row);
        LoversKeyboard.setKeyboard(keyboard);

        return LoversKeyboard;
    }


    public ReplyKeyboardMarkup getAuthenticateKeyboard() {
        ReplyKeyboardMarkup authenticateKeyboard = new ReplyKeyboardMarkup();
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

    public ReplyKeyboardMarkup getMainMenu() {
        ReplyKeyboardMarkup mainMenuKeyboard = new ReplyKeyboardMarkup();
        mainMenuKeyboard.setSelective(true);
        mainMenuKeyboard.setResizeKeyboard(true);
        mainMenuKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(messagesService.getText("button.search")));
        row.add(new KeyboardButton(messagesService.getText("button.profile")));
        row.add(new KeyboardButton(messagesService.getText("button.lovers")));
        keyboard.add(row);
        mainMenuKeyboard.setKeyboard(keyboard);

        return mainMenuKeyboard;
    }

    public ReplyKeyboardMarkup getProfileEditMenu() {
        ReplyKeyboardMarkup mainMenuKeyboard = new ReplyKeyboardMarkup();
        mainMenuKeyboard.setSelective(true);
        mainMenuKeyboard.setResizeKeyboard(true);
        mainMenuKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();
        KeyboardRow row5 = new KeyboardRow();


        row1.add(new KeyboardButton(messagesService.getText("button.editSex")));
        row2.add(new KeyboardButton(messagesService.getText("button.editName")));
        row3.add(new KeyboardButton(messagesService.getText("button.editDescription")));
        row4.add(new KeyboardButton(messagesService.getText("button.editFindSex")));
        row5.add(new KeyboardButton(messagesService.getText("button.menu")));
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);
        keyboard.add(row5);
        mainMenuKeyboard.setKeyboard(keyboard);

        return mainMenuKeyboard;
    }

    public ReplyKeyboardMarkup getProfileMenu() {
        ReplyKeyboardMarkup searchKeyboard = new ReplyKeyboardMarkup();
        searchKeyboard.setSelective(true);
        searchKeyboard.setResizeKeyboard(true);
        searchKeyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(messagesService.getText("button.edit")));
        row.add(new KeyboardButton(messagesService.getText("button.menu")));
        keyboard.add(row);
        searchKeyboard.setKeyboard(keyboard);
        return searchKeyboard;
    }


}
