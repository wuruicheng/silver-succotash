package com.FileManager;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperations {

    // 查找文件
    public static List<File> searchFiles(File directory, String fileName) {
        List<File> result = new ArrayList<>();
        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return result;
        }

        searchFilesRecursive(directory, fileName.toLowerCase(), result);
        return result;
    }

    private static void searchFilesRecursive(File directory, String fileName, List<File> result) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().toLowerCase().contains(fileName)) {
                result.add(file);
            }
            if (file.isDirectory()) {
                searchFilesRecursive(file, fileName, result);
            }
        }
    }

    // 复制文件
    public static boolean copyFile(File source, File destination) {
        try {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "复制文件失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 复制文件夹
    public static boolean copyDirectory(File source, File destination) {
        try {
            Files.walk(source.toPath())
                    .forEach(sourcePath -> {
                        Path targetPath = destination.toPath().resolve(source.toPath().relativize(sourcePath));
                        try {
                            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "复制文件夹失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 预览文本文件
    public static String previewTextFile(File file, int maxLines) {
        StringBuilder content = new StringBuilder();
        String[] textExtensions = {".txt", ".java", ".ini", ".bat", ".properties", ".xml", ".html", ".css", ".js"};

        boolean isTextFile = false;
        for (String ext : textExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                isTextFile = true;
                break;
            }
        }

        if (!isTextFile) {
            return "不支持预览此文件类型";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < maxLines) {
                content.append(line).append("\n");
                lineCount++;
            }
            if (lineCount >= maxLines) {
                content.append("\n... (仅显示前").append(maxLines).append("行)");
            }
        } catch (IOException e) {
            return "读取文件失败: " + e.getMessage();
        }

        return content.toString();
    }

    // 重命名文件
    public static boolean renameFile(File oldFile, String newName) {
        File newFile = new File(oldFile.getParent(), newName);
        if (newFile.exists()) {
            JOptionPane.showMessageDialog(null, "文件已存在",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return oldFile.renameTo(newFile);
    }

    // 统计文件夹
    public static int[] countFilesAndFolders(File directory) {
        int[] counts = new int[2]; // [0]: 文件数, [1]: 文件夹数
        if (!directory.exists() || !directory.isDirectory()) {
            return counts;
        }

        File[] files = directory.listFiles();
        if (files == null) return counts;

        for (File file : files) {
            if (file.isDirectory()) {
                counts[1]++;
                int[] subCounts = countFilesAndFolders(file);
                counts[0] += subCounts[0];
                counts[1] += subCounts[1];
            } else {
                counts[0]++;
            }
        }

        return counts;
    }

    // 获取文件大小格式
    public static String getFileSize(File file) {
        long size = file.length();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    // 递归删除目录
    public static boolean deleteDirectory(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }
}