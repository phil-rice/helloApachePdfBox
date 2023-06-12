package org.hcl.pdftemplate.freeChart;

import java.awt.*;
import java.awt.image.BufferedImage;

 interface BufferedImageUtils {
    public static BufferedImage removeAlphaChannel(BufferedImage img,
                                                   int color) {
        if (!img.getColorModel().hasAlpha()) {
            return img;
        }

        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = target.createGraphics();
        g.setColor(new Color(color, false));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return target;
    }

     static BufferedImage createImage(int width, int height,
                                            boolean hasAlpha) {
        return new BufferedImage(width, height,
                hasAlpha ? BufferedImage.TYPE_INT_ARGB
                        : BufferedImage.TYPE_INT_RGB);
    }


}
