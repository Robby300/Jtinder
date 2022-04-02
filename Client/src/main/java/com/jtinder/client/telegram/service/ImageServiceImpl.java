package com.jtinder.client.telegram.service;

import com.jtinder.client.domen.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    @Value("${upload.path}")    // todo перепроверить работу на доккере
    private String uploadPath;
    private final PrerevolutionaryTranslator translator;

    public ImageServiceImpl(PrerevolutionaryTranslator translator) {
        this.translator = translator;
    }

    public File getFile(Profile profile) throws IOException {
        File file = new File(uploadPath);
        BufferedImage image = ImageIO.read(file);
        Font header = new Font("Old Standard TT", Font.BOLD, 44);
        Font body = new Font("Old Standard TT", Font.PLAIN, 30);
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);

//        g.setFont(body);
//        g.drawString(translator.translate(userService.findAllWeLike().contains(profile) ? "Любо": "Нэ любо"), 60, 50);

        g.setFont(header);
        g.drawString(translator.translate(profile.getDescription()), 60, 130);

        g.setFont(body);
        FontMetrics fm = g.getFontMetrics(body);
        int lineHeight = fm.getHeight();
        String textToDraw = translator.translate(profile.getDescription());
        String[] arr = textToDraw.split(" ");
        int nIndex = 0;
        int startX = 60;
        int startY = 190;
        while (nIndex < arr.length) {
            String line = arr[nIndex++];
            while ((nIndex < arr.length) && (fm.stringWidth(line + " " + arr[nIndex]) < 447)) {
                line = line + " " + arr[nIndex];
                nIndex++;
            }
            g.drawString(line, startX, startY);
            startY = startY + lineHeight;
        }
        File file1 = new File(file.getParentFile(), "result_image.jpg");
        ImageIO.write(image, "jpg", file1);
        return file1;
    }

}
