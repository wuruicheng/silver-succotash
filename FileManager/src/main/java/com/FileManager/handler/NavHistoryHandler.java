package com.FileManager.handler;

import com.FileManager.gui.FileManagerGUI;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel; // 确保导入完整包名的TreeModel
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 负责：目录导航+历史记录管理核心逻辑
 */
public class NavHistoryHandler {
    private final FileManagerGUI mainFrame;
    private final FileTreeHandler treeHandler;
    private final FileTableHandler tableHandler;

    public NavHistoryHandler(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
        this.treeHandler = new FileTreeHandler(mainFrame);
        this.tableHandler = new FileTableHandler(mainFrame);
        mainFrame.backHistory = new ArrayList<>();
        mainFrame.forwardHistory = new ArrayList<>();
    }

    public void setCurrentDirectory(File directory) {
        mainFrame.currentDirectory = directory;
        mainFrame.pathField.setText(directory.getAbsolutePath());
        tableHandler.refreshFileTable(directory);
        treeHandler.updateTreeSelection(directory);
    }

    public void refreshCurrentDirectory() {
        if (mainFrame.currentDirectory == null) return;
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                tableHandler.refreshFileTable(mainFrame.currentDirectory);
                // 修复：强转类型为javax.swing.tree.DefaultTreeModel
                DefaultTreeModel treeModel = (DefaultTreeModel) mainFrame.treeModel;
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
                treeHandler.findAndSelectNode(rootNode, mainFrame.currentDirectory);
                return null;
            }

            @Override
            protected void done() {
                mainFrame.previewArea.setText("");
                JOptionPane.showMessageDialog(mainFrame, "目录已成功刷新", "刷新完成", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    public void navigateBack() {
        if (mainFrame.backHistory.size() <= 1) {
            JOptionPane.showMessageDialog(mainFrame, "已到最开始的目录，无法后退", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File current = mainFrame.backHistory.remove(mainFrame.backHistory.size() - 1);
        mainFrame.forwardHistory.add(current);
        File targetDir = mainFrame.backHistory.get(mainFrame.backHistory.size() - 1);
        setCurrentDirectory(targetDir);
        updateNavButtonStatus();
    }

    public void navigateForward() {
        if (mainFrame.forwardHistory.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "没有前进的目录，无法前进", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File targetDir = mainFrame.forwardHistory.remove(mainFrame.forwardHistory.size() - 1);
        mainFrame.backHistory.add(mainFrame.currentDirectory);
        setCurrentDirectory(targetDir);
        updateNavButtonStatus();
    }

    public void updateNavButtonStatus() {
        mainFrame.backButton.setEnabled(mainFrame.backHistory.size() > 1);
        mainFrame.forwardButton.setEnabled(!mainFrame.forwardHistory.isEmpty());
    }
}