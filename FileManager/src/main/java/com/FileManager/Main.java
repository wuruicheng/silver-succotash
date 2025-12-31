package com.FileManager;

import com.FileManager.gui.FileManagerGUI;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        // ============ 自动从JAR资源中提取SQL文件 ============
        try {
            // 先检查是否已有SQL文件在当前目录
            File localSqlFile = new File("file_manager_db.sql");

            if (!localSqlFile.exists()) {
                System.out.println("正在从程序内部提取SQL文件...");
                extractSqlFromJar();
            } else {
                System.out.println("使用现有SQL文件: " + localSqlFile.getAbsolutePath());
            }

            // 初始化数据库（使用当前目录的SQL文件）
            DBUtil.initDatabaseFromSqlFile("file_manager_db.sql");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "数据库初始化失败！\n错误：" + e.getMessage(),
                    "启动错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // 启动文件管理器GUI界面
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileManagerGUI frame = new FileManagerGUI();
            frame.setVisible(true);
        });
    }

    /**
     * 从JAR内部资源中提取SQL文件到当前目录
     */
    private static void extractSqlFromJar() throws IOException {
        // 从JAR的资源中读取SQL文件
        InputStream inputStream = Main.class.getClassLoader()
                .getResourceAsStream("file_manager_db.sql");

        if (inputStream == null) {
            throw new IOException("程序内部找不到SQL文件资源");
        }

        // 写入到当前目录
        try (OutputStream outputStream = new FileOutputStream("file_manager_db.sql")) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        System.out.println("SQL文件已提取到: " + new File("file_manager_db.sql").getAbsolutePath());
    }
}