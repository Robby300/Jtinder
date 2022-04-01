package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.Profile;

import java.io.File;
import java.io.IOException;

public interface ImageService {
    File getFile(Profile user) throws IOException;
}
