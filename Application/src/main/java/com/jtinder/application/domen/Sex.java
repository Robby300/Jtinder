package com.jtinder.application.domen;

import lombok.Getter;

@Getter
public enum Sex {
    MALE("Сударь"),
    FEMALE("Сударыня");

    private final String name;

    Sex(String name) {
        this.name = name;
    }
}
