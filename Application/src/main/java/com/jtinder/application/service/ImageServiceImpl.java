package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${upload.path}")
    private String uploadPath;

    private final PrerevolutionaryTranslator translator;

    public ImageServiceImpl(PrerevolutionaryTranslator translator) {
        this.translator = translator;
    }


    public void getFile(User user) throws IOException {
        File file = new File(uploadPath);
        BufferedImage image = ImageIO.read(file);
        Font header = new Font("Old Standard TT", Font.BOLD, 44);
        Font body = new Font("Old Standard TT", Font.BOLD, 30);
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);

        g.setFont(header);
        g.drawString(translator.translate(user.getName()), 50, 50);

        g.setFont(body);
        g.drawString(translator.translate(user.getName()), 50, 88);

        ImageIO.write(image, "jpg", new File(file.getParentFile(), "result_image.jpg"));
    }
}
