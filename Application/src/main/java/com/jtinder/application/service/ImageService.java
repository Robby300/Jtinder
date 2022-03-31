package com.jtinder.application.service;

import com.jtinder.application.domen.User;

import java.io.IOException;

public interface ImageService {
    public void getFile(User user) throws IOException;
}
