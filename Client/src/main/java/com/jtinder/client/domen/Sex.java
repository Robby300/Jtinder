package com.jtinder.client.domen;

import lombok.Getter;

@Getter
public enum Sex {
    MALE("Сударь"),
    FEMALE("Сударыня");

    private String name;

    Sex(String name) {
        this.name = name;
    }
}
