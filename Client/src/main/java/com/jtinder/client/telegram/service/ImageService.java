package com.jtinder.client.telegram.service;

import com.jtinder.client.domain.Profile;

import java.io.File;

public interface ImageService {
    File getFile(Profile user);
}
