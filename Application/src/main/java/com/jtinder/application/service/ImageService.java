package com.jtinder.application.service;

import com.jtinder.application.domain.User;

import java.io.File;
import java.io.IOException;

public interface ImageService {
    File getFile(User user) throws IOException;
}
