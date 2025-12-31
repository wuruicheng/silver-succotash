package com.FileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageLoader {

    // 创建自定义图标
    public static Icon createFolderIcon(Color color) {
        return new ImageIcon(createFolderImage(16, 16, color));
    }

    public static Icon createFileIcon(Color color) {
        return new ImageIcon(createFileImage(16, 16, color));
    }

    public static Icon createTextFileIcon() {
        return new ImageIcon(createTextFileImage(16, 16));
    }

    public static Icon createJavaFileIcon() {
        return new ImageIcon(createJavaFileImage(16, 16));
    }

    public static Icon createImageIcon() {
        return new ImageIcon(createImageFileImage(16, 16));
    }

    public static Icon createPDFIcon() {
        return new ImageIcon(createPDFImage(16, 16));
    }

    private static Image createFolderImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制文件夹图标
        g2d.setColor(color);
        g2d.fillRoundRect(2, 4, width-4, height-6, 4, 4);
        g2d.setColor(color.darker());
        g2d.fillRect(4, 2, width-8, 4);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(2, 4, width-4, height-6, 4, 4);
        g2d.drawRect(4, 2, width-8, 4);

        g2d.dispose();
        return image;
    }

    private static Image createFileImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制文件图标
        g2d.setColor(Color.WHITE);
        g2d.fillRect(2, 2, width-4, height-4);
        g2d.setColor(color);
        g2d.fillRect(2, 2, 8, height-4);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(2, 2, width-4, height-4);

        g2d.dispose();
        return image;
    }

    private static Image createTextFileImage(int width, int height) {
        return createFileImage(width, height, new Color(100, 149, 237)); // 蓝色
    }

    private static Image createJavaFileImage(int width, int height) {
        return createFileImage(width, height, new Color(220, 20, 60)); // 红色
    }

    private static Image createImageFileImage(int width, int height) {
        return createFileImage(width, height, new Color(50, 205, 50)); // 绿色
    }

    private static Image createPDFImage(int width, int height) {
        return createFileImage(width, height, new Color(255, 69, 0)); // 橙色
    }
}