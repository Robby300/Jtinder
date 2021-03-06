package com.jtinder.client.telegram.service;


import com.jtinder.client.domain.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
    @Value("${upload.path}")    // todo перепроверить работу на доккере
    private String uploadPath;
    private final PrerevolutionaryTranslator translator;

    public ImageServiceImpl(PrerevolutionaryTranslator translator) {
        this.translator = translator;
    }

    public File getFile(Profile profile) {
        File result = null;
        try {
            File backgroundImage = ResourceUtils.getFile(uploadPath);
            BufferedImage image = ImageIO.read(backgroundImage);
            Font header = new Font("Old Standard TT", Font.BOLD, 44);
            Font body = new Font("Old Standard TT", Font.PLAIN, 30);
            int leftOffset = (int) (image.getWidth() * 0.1);
            int topOffset = (int) (image.getHeight() * 0.1) + (header.getSize() / 2);
            int maxLineWidth = image.getWidth() - 2 * leftOffset;
            log.info("leftOffset: {}, topOffset: {}, maxLineWidth: {}", leftOffset, topOffset, maxLineWidth);
            Graphics graphics = image.getGraphics();
            graphics.setColor(Color.BLACK);
            graphics.setFont(header);
            List<String> linesToWrite = getLinesToWrite(translator.translate(profile.getDescription()), graphics, maxLineWidth, header, body);
            writeLinesToImage(body, leftOffset, topOffset, graphics, linesToWrite);
            result = new File(backgroundImage.getParentFile(), "result_image.jpg");
            ImageIO.write(image, "jpg", result);
        } catch (IOException e) {
            log.error("Ошибка чтения/записи файла " + e);
        }
        return result;
    }

    private void writeLinesToImage(Font body, int leftOffset, int topOffset, Graphics graphics, List<String> linesToWrite) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        if (linesToWrite.size() == 1) {
            String[] words = linesToWrite.get(0).split("\\s");
            graphics.drawString(words[0], leftOffset, topOffset);
            String descLine = Arrays.stream(words).skip(1).collect(Collectors.joining(" "));
            graphics.setFont(body);
            fontMetrics = graphics.getFontMetrics();
            topOffset += fontMetrics.getHeight();
            graphics.drawString(descLine, leftOffset, topOffset);
        } else {
            for (int i = 0; i < linesToWrite.size(); i++) {
                if (i == 0) {
                    graphics.drawString(linesToWrite.get(i), leftOffset, topOffset);
                    graphics.setFont(body);
                    fontMetrics = graphics.getFontMetrics();
                } else {
                    graphics.drawString(linesToWrite.get(i), leftOffset, topOffset);
                }
                topOffset += fontMetrics.getHeight();
            }
        }
    }

    private List<String> getLinesToWrite(String text, Graphics graphics, int maxLineWidth, Font header, Font body) {
        String[] words = text.split("\\s");
        List<String> linesToWrite = new ArrayList<>();
        FontMetrics fontMetrics = graphics.getFontMetrics(header);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; ) {
            sb.append(words[i++]);
            while ((i < words.length) && (fontMetrics.stringWidth(sb + " " + words[i]) < maxLineWidth)) {
                sb.append(" ");
                sb.append(words[i++]);
            }
            if (fontMetrics.getFont().equals(header)) {
                fontMetrics = graphics.getFontMetrics(body);
            }
            linesToWrite.add(sb.toString());
            sb.setLength(0);
        }
        return linesToWrite;
    }
}