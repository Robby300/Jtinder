package com.jtinder.application.service;

import com.jtinder.application.domen.User;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageServiceImpl implements ImageService {

    private final File file = new File("/home/robert/IdeaProjects/Jtinder/Application/src/main/resources/file/prerev-background.jpg");
    private final PrerevolutionaryTranslator translator = new PrerevolutionaryTranslator();

    public void getFile(User user) throws IOException {
        Font header = new Font("Old Standard TT", Font.BOLD, 48);
        Font description = new Font("Old Standard TT", Font.BOLD, 24);
        Font old_standard_tt = Font.getFont("Old Standard TT");
        BufferedImage image = ImageIO.read(file);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        g.setFont(old_standard_tt);
        g.drawString(translator.translate(user.getName() + ","), (626 - g.getFontMetrics(header).stringWidth(user.getName())) / 2, 50);

    }
}
