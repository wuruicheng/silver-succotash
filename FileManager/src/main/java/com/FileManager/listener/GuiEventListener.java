package com.FileManager.listener;

import com.FileManager.gui.FileManagerGUI;
import com.FileManager.gui.OperationLogWindow;
import com.FileManager.handler.FileOptHandler;
import com.FileManager.handler.FileTableHandler;
import com.FileManager.handler.FileTreeHandler;
import com.FileManager.handler.NavHistoryHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * 负责：全局所有组件的事件绑定
 * 包含：按钮/树/表格/菜单/右键弹窗 的所有点击/选择/双击事件
 * 事件触发后，调用对应handler的业务方法，本类只做事件分发，不写业务逻辑
 */
public class GuiEventListener {
    private final FileManagerGUI mainFrame;
    private final FileTreeHandler treeHandler;
    private final FileTableHandler tableHandler;
    private final NavHistoryHandler navHandler;
    private final FileOptHandler fileOptHandler;

    public GuiEventListener(FileManagerGUI mainFrame, FileTreeHandler treeHandler, FileTableHandler tableHandler, NavHistoryHandler navHandler, FileOptHandler fileOptHandler) {
        this.mainFrame = mainFrame;
        this.treeHandler = treeHandler;
        this.tableHandler = tableHandler;
        this.navHandler = navHandler;
        this.fileOptHandler = fileOptHandler;
    }

    public void bindAllListeners() {
        bindSearchListener(); bindNavButtonListener(); bindPathFieldListener();
        bindTreeListener(); bindTableListener(); bindMenuListener();
    }

    private void bindSearchListener() {
        mainFrame.searchButton.addActionListener(e -> fileOptHandler.searchFiles());
        mainFrame.searchField.addActionListener(e -> fileOptHandler.searchFiles());
    }

    private void bindNavButtonListener() {
        mainFrame.backButton.addActionListener(e -> navHandler.navigateBack());
        mainFrame.forwardButton.addActionListener(e -> navHandler.navigateForward());
        mainFrame.refreshButton.addActionListener(e -> navHandler.refreshCurrentDirectory());
    }

    private void bindPathFieldListener() {
        mainFrame.pathField.addActionListener(e -> {
            File newDir = new File(mainFrame.pathField.getText());
            if (newDir.exists() && newDir.isDirectory()) {
                mainFrame.forwardHistory.clear();
                mainFrame.backHistory.add(mainFrame.currentDirectory);
                navHandler.setCurrentDirectory(newDir);
                navHandler.updateNavButtonStatus();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "目录不存在", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void bindTreeListener() {
        mainFrame.fileTree.addTreeSelectionListener(e -> {
            var path = mainFrame.fileTree.getSelectionPath();
            if (path != null) {
                var node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof File) {
                    File file = (File) node.getUserObject();
                    if (file.isDirectory()) {
                        mainFrame.forwardHistory.clear();
                        mainFrame.backHistory.add(mainFrame.currentDirectory);
                        navHandler.setCurrentDirectory(file);
                        navHandler.updateNavButtonStatus();
                    } else {
                        tableHandler.showFilePreview(file);
                    }
                }
            }
        });
        mainFrame.fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    var path = mainFrame.fileTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        var node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node.getUserObject() instanceof File) {
                            File file = (File) node.getUserObject();
                            if (file.isDirectory()) {
                                mainFrame.forwardHistory.clear();
                                mainFrame.backHistory.add(mainFrame.currentDirectory);
                                navHandler.setCurrentDirectory(file);
                                navHandler.updateNavButtonStatus();
                            }
                        }
                    }
                }
            }
        });
    }

    private void bindTableListener() {
        // 表格选择事件
        mainFrame.fileTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int viewRow = mainFrame.fileTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
                    DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
                    String fileName = (String) tableModel.getValueAt(modelRow, 0);
                    if (fileName.equals("..")) return;

                    File file = new File(mainFrame.currentDirectory, fileName);
                    if (!file.isDirectory()) {
                        tableHandler.showFilePreview(file);
                    }
                }
            }
        });
        // 表格双击事件
        mainFrame.fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = mainFrame.fileTable.getSelectedRow();
                    if (viewRow >= 0) {
                        int modelRow = mainFrame.fileTable.convertRowIndexToModel(viewRow);
                        DefaultTableModel tableModel = (DefaultTableModel) mainFrame.fileTable.getModel();
                        String fileName = (String) tableModel.getValueAt(modelRow, 0);

                        if (fileName.equals("..")) {
                            if (mainFrame.currentDirectory.getParent() != null) {
                                mainFrame.forwardHistory.clear();
                                mainFrame.backHistory.add(mainFrame.currentDirectory);
                                navHandler.setCurrentDirectory(mainFrame.currentDirectory.getParentFile());
                                navHandler.updateNavButtonStatus();
                            }
                            return;
                        }

                        File file = new File(mainFrame.currentDirectory, fileName);
                        if (file.isDirectory()) {
                            mainFrame.forwardHistory.clear();
                            mainFrame.backHistory.add(mainFrame.currentDirectory);
                            navHandler.setCurrentDirectory(file);
                            navHandler.updateNavButtonStatus();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {if (e.isPopupTrigger()) showTablePopupMenu(e);}
            @Override
            public void mouseReleased(MouseEvent e) {if (e.isPopupTrigger()) showTablePopupMenu(e);}
        });
    }

    private void showTablePopupMenu(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem copy = new JMenuItem("复制");
        JMenuItem cut = new JMenuItem("剪切");
        JMenuItem paste = new JMenuItem("粘贴");
        JMenuItem rename = new JMenuItem("重命名");
        JMenuItem delete = new JMenuItem("删除");
        popup.add(copy);popup.add(cut);popup.add(paste);popup.addSeparator();popup.add(rename);popup.add(delete);

        copy.addActionListener(ev -> fileOptHandler.copySelectedFile());
        cut.addActionListener(ev -> fileOptHandler.cutSelectedFile());
        paste.addActionListener(ev -> fileOptHandler.pasteFile());
        rename.addActionListener(ev -> fileOptHandler.renameSelectedFile());
        delete.addActionListener(ev -> fileOptHandler.deleteSelectedFile());

        popup.show(mainFrame.fileTable, e.getX(), e.getY());
    }

    private void bindMenuListener() {
        JMenuBar menuBar = mainFrame.getJMenuBar();
        JMenu fileMenu = (JMenu) menuBar.getMenu(0);
        JMenu editMenu = (JMenu) menuBar.getMenu(1);
        JMenu viewMenu = (JMenu) menuBar.getMenu(2);
        JMenu toolsMenu = (JMenu) menuBar.getMenu(3);

        fileMenu.getItem(0).addActionListener(e -> fileOptHandler.copySelectedFile());
        fileMenu.getItem(1).addActionListener(e -> fileOptHandler.cutSelectedFile());
        fileMenu.getItem(2).addActionListener(e -> fileOptHandler.pasteFile());
        fileMenu.getItem(4).addActionListener(e -> fileOptHandler.renameSelectedFile());
        fileMenu.getItem(5).addActionListener(e -> fileOptHandler.deleteSelectedFile());
        fileMenu.getItem(7).addActionListener(e -> System.exit(0));

        editMenu.getItem(0).addActionListener(e -> mainFrame.searchField.requestFocus());
        viewMenu.getItem(0).addActionListener(e -> navHandler.refreshCurrentDirectory());
        toolsMenu.getItem(0).addActionListener(e -> fileOptHandler.showFolderStatistics());
        toolsMenu.getItem(1).addActionListener(e -> fileOptHandler.showSearchDialog());
        toolsMenu.getItem(2).addActionListener(e -> new OperationLogWindow(mainFrame).setVisible(true));
    }
}