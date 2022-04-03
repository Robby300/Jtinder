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
    @Value("${upload.path}")    // todo перепроверить работу на доккере
    private String uploadPath;
    private final PrerevolutionaryTranslator translator;

    public ImageServiceImpl(PrerevolutionaryTranslator translator) {
        this.translator = translator;
    }

    public File getFile(User user) throws IOException {
        File file = new File(uploadPath);
        BufferedImage image = ImageIO.read(file);
        Font header = new Font("Old Standard TT", Font.BOLD, 44);
        Font body = new Font("Old Standard TT", Font.PLAIN, 33);
        Graphics g = image.getGraphics();
        g.setColor(Color.BLACK);

//        g.setFont(body);
//        g.drawString(translator.translate(userService.findAllWeLike().contains(profile) ? "Любо": "Нэ любо"), 60, 50);

        FontMetrics fm = g.getFontMetrics(body);
        int lineHeight = fm.getHeight();
        String textToDraw = translator.translate(user.getDescription());

        String[] arr = textToDraw.split(" ");
        int nIndex = 0;
        int startX = 60;
        int startY = 190;
        int i = 0;

        while (nIndex < arr.length) {

            String line = arr[nIndex++];
            while ((nIndex < arr.length) && (fm.stringWidth(line + " " + arr[nIndex]) < 480)) {
                line = line + " " + arr[nIndex];
                nIndex++;
            }
            if (i == 0) {
                g.setFont(header);
                g.drawString(line, startX, startY);
            } else {
                g.setFont(body);
                g.drawString(line, startX, startY);
            }
            startY = startY + lineHeight;
            i++;

        }
        File file1 = new File(file.getParentFile(), "result_image.jpg");
        ImageIO.write(image, "jpg", file1);
        return file1;
    }
}
