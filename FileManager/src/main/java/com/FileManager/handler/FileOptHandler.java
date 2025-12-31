package com.FileManager.handler;

import com.FileManager.gui.FileManagerGUI;
import com.FileManager.FileOperations;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.io.File;
import java.util.List;

/**
 * 负责：所有文件操作核心逻辑
 * 包含：复制/剪切/粘贴/重命名/删除、搜索文件、统计文件夹、日志记录
 */
public class FileOptHandler {
    private final FileManagerGUI mainFrame;
    private final NavHistoryHandler navHandler;

    public FileOptHandler(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
        this.navHandler = new NavHistoryHandler(mainFrame);
    }

    public void copySelectedFile() {
        int viewRow = mainFrame.fileTable.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
            String fileName = (String) tableModel.getValueAt(modelRow, 0);
            if (fileName.equals("..")) return;

            mainFrame.clipboardFile = new File(mainFrame.currentDirectory, fileName);
            mainFrame.isCutOperation = false;
            logOperation("复制", mainFrame.clipboardFile.getAbsolutePath(), "成功", "复制到剪贴板");
            JOptionPane.showMessageDialog(mainFrame, "已复制: " + fileName, "复制", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void cutSelectedFile() {
        int viewRow = mainFrame.fileTable.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
            String fileName = (String) tableModel.getValueAt(modelRow, 0);
            if (fileName.equals("..")) return;

            mainFrame.clipboardFile = new File(mainFrame.currentDirectory, fileName);
            mainFrame.isCutOperation = true;
            logOperation("剪切", mainFrame.clipboardFile.getAbsolutePath(), "成功", "剪切到剪贴板");
            JOptionPane.showMessageDialog(mainFrame, "已剪切: " + fileName, "剪切", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void pasteFile() {
        if (mainFrame.clipboardFile == null) {
            JOptionPane.showMessageDialog(mainFrame, "剪贴板为空", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File destination = new File(mainFrame.currentDirectory, mainFrame.clipboardFile.getName());
        boolean success = false;
        String detail = "从" + mainFrame.clipboardFile.getAbsolutePath() + "粘贴到" + destination.getAbsolutePath();

        if (mainFrame.clipboardFile.isDirectory()) {
            success = FileOperations.copyDirectory(mainFrame.clipboardFile, destination);
            if (success && mainFrame.isCutOperation) {
                success = FileOperations.deleteDirectory(mainFrame.clipboardFile);
                detail += "，原文件已删除（剪切）";
            }
        } else {
            success = FileOperations.copyFile(mainFrame.clipboardFile, destination);
            if (success && mainFrame.isCutOperation) {
                success = mainFrame.clipboardFile.delete();
                detail += "，原文件已删除（剪切）";
            }
        }

        logOperation("粘贴", destination.getAbsolutePath(), success ? "成功" : "失败", detail);
        if (mainFrame.isCutOperation) mainFrame.clipboardFile = null;
        navHandler.refreshCurrentDirectory();
    }

    public void renameSelectedFile() {
        int viewRow = mainFrame.fileTable.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
            String oldName = (String) tableModel.getValueAt(modelRow, 0);
            if (oldName.equals("..")) return;

            File oldFile = new File(mainFrame.currentDirectory, oldName);
            String newName = JOptionPane.showInputDialog(this.mainFrame, "输入新名称:", oldName);
            if (newName != null && !newName.trim().isEmpty()) {
                boolean success = FileOperations.renameFile(oldFile, newName.trim());
                String detail = "原名称：" + oldName + "，新名称：" + newName.trim();
                logOperation("重命名", oldFile.getAbsolutePath(), success ? "成功" : "失败", detail);
                if (success) navHandler.refreshCurrentDirectory();
            }
        }
    }

    public void deleteSelectedFile() {
        int viewRow = mainFrame.fileTable.getSelectedRow();
        if (viewRow >= 0) {
            int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
            DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
            String fileName = (String) tableModel.getValueAt(modelRow, 0);
            if (fileName.equals("..")) return;

            File file = new File(mainFrame.currentDirectory, fileName);
            int confirm = JOptionPane.showConfirmDialog(this.mainFrame, "确定要删除 " + fileName + " 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = file.isDirectory() ? FileOperations.deleteDirectory(file) : file.delete();
                logOperation("删除", file.getAbsolutePath(), success ? "成功" : "失败", "");
                navHandler.refreshCurrentDirectory();
            }
        }
    }

    public void searchFiles() {
        String searchText = mainFrame.searchField.getText().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "请输入搜索内容", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<File> results = FileOperations.searchFiles(mainFrame.currentDirectory, searchText);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "未找到匹配的文件", "搜索结果", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder("找到 " + results.size() + " 个文件:\n\n");
            for (File file : results) {
                message.append(file.getAbsolutePath()).append("\n");
            }
            JTextArea textArea = new JTextArea(message.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(mainFrame, scrollPane, "搜索结果", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void showFolderStatistics() {
        int[] counts = FileOperations.countFilesAndFolders(mainFrame.currentDirectory);
        String message = String.format("文件夹统计:\n\n文件数: %d\n文件夹数: %d\n总计: %d",counts[0], counts[1], counts[0] + counts[1]);
        JOptionPane.showMessageDialog(mainFrame, message, "文件夹统计", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showSearchDialog() {
        JTextField searchDirField = new JTextField(mainFrame.currentDirectory.getAbsolutePath(), 30);
        JTextField fileNameField = new JTextField(20);
        JPanel panel = new JPanel(new java.awt.GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("搜索目录:"));panel.add(searchDirField);
        panel.add(new JLabel("文件名:"));panel.add(fileNameField);
        panel.add(new JLabel("(支持模糊搜索)"));

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "搜索文件",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            File searchDir = new File(searchDirField.getText());
            if (!searchDir.exists() || !searchDir.isDirectory()) {
                JOptionPane.showMessageDialog(mainFrame, "目录不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<File> results = FileOperations.searchFiles(searchDir, fileNameField.getText().trim());
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "未找到匹配的文件", "搜索结果", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder();
                for (File file : results) {
                    message.append(file.getAbsolutePath()).append("\n");
                }
                JTextArea textArea = new JTextArea(message.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(mainFrame, scrollPane, "搜索结果", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // 异步记录操作日志
    public void logOperation(String operationType, String filePath, String result, String detail) {
        javax.swing.SwingWorker<Boolean, Void> worker = new javax.swing.SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return mainFrame.logDAO.addOperationLog(operationType, filePath, result, detail);
            }
            @Override
            protected void done() {
                try {if (!get()) System.err.println("日志记录失败："+operationType+" "+filePath);}
                catch (Exception e) {e.printStackTrace();}
            }
        };
        worker.execute();
    }
}