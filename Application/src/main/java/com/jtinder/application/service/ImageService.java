package com.jtinder.application.service;

import com.jtinder.application.domen.User;

import java.io.File;
import java.io.IOException;

public interface ImageService {
    File getFile(User user) throws IOException;
}
