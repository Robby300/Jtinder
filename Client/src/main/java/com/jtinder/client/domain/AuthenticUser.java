package com.jtinder.client.domain;

import lombok.Data;


@Data
public class AuthenticUser {
    private final Long username;
    private final String password;
}
