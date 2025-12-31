package com.FileManager.gui;

import com.FileManager.FileOperationLogDAO;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import com.FileManager.component.GuiComponentInit;
import com.FileManager.handler.FileOptHandler;
import com.FileManager.handler.FileTableHandler;
import com.FileManager.handler.FileTreeHandler;
import com.FileManager.handler.NavHistoryHandler;
import com.FileManager.listener.GuiEventListener;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * 拆分后的主窗口类 - 极简，只做全局变量声明+初始化调度
 * 核心功能全部解耦到各个handler/listener/component包中
 */
public class FileManagerGUI extends JFrame {
    // ========== 全局公共组件（给其他类调用） ==========
    public DefaultTableModel tableModel;
    public JTable fileTable;
    public final String[] columnNames = {"名称", "大小", "类型", "修改日期"};
    public DefaultTreeModel treeModel;
    public JTree fileTree;
    public JTextArea previewArea;
    public JTextField searchField;
    public JTextField pathField;
    public JButton searchButton;
    public JButton backButton;
    public JButton forwardButton;
    public JButton refreshButton;

    public File currentDirectory;
    public File clipboardFile;
    public boolean isCutOperation;
    public FileOperationLogDAO logDAO;
    public List<File> backHistory;
    public List<File> forwardHistory;

    // ========== 功能模块对象 ==========
    private GuiComponentInit componentInit;
    private FileTreeHandler treeHandler;
    private FileTableHandler tableHandler;
    private NavHistoryHandler navHandler;
    private FileOptHandler fileOptHandler;
    private GuiEventListener eventListener;

    public FileManagerGUI() {
        // 1. 初始化基础数据
        logDAO = new FileOperationLogDAO();
        // 2. 初始化所有GUI组件+布局
        componentInit = new GuiComponentInit(this);
        componentInit.initAllComponents();
        // 3. 初始化各功能处理器
        treeHandler = new FileTreeHandler(this);
        tableHandler = new FileTableHandler(this);
        navHandler = new NavHistoryHandler(this);
        fileOptHandler = new FileOptHandler(this);
        // 4. 加载文件树
        treeHandler.loadFileTree();
        // 5. 初始化初始目录+历史记录
        File initDir = new File(System.getProperty("user.home"));
        navHandler.setCurrentDirectory(initDir);
        backHistory.add(initDir);
        navHandler.updateNavButtonStatus();
        // 6. 绑定所有事件监听器
        eventListener = new GuiEventListener(this, treeHandler, tableHandler, navHandler, fileOptHandler);
        eventListener.bindAllListeners();
    }
}