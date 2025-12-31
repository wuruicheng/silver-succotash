package com.FileManager.handler;

import com.FileManager.gui.FileManagerGUI;
import com.FileManager.FileOperations;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 负责：文件表格的所有逻辑
 * 包含：加载目录文件、刷新表格、显示文件预览
 */
public class FileTableHandler {
    private final FileManagerGUI mainFrame;
    private final SimpleDateFormat sdf;

    public FileTableHandler(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
        this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    // 加载指定目录的文件到表格
    public void refreshFileTable(File directory) {
        DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
        tableModel.setRowCount(0);

        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            return;
        }

        if (directory.getParent() != null) {
            tableModel.addRow(new Object[]{"..", "", "上级目录", ""});
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String fileType = file.isDirectory() ? "文件夹" : "文件";
                String fileSize = file.isDirectory() ? "" : FileOperations.getFileSize(file);
                String modifyTime = sdf.format(new Date(file.lastModified()));
                tableModel.addRow(new Object[]{fileName, fileSize, fileType, modifyTime});
            }
        }
    }

    // 修复：替换不存在的readFileContent为FileOperations已有的previewTextFile
    public void showFilePreview(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            mainFrame.previewArea.setText("");
            return;
        }
        String previewContent = FileOperations.previewTextFile(file, 100);
        mainFrame.previewArea.setText(previewContent);
    }
}