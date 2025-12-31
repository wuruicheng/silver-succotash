package com.FileManager.component;

import com.FileManager.gui.FileManagerGUI;
import com.FileManager.FileTreeRenderer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel; // 补充导入DefaultTableModel
import java.awt.*;

/**
 * 负责：所有界面组件的创建、样式设置、布局排版
 */
public class GuiComponentInit {
    private final FileManagerGUI mainFrame;

    public GuiComponentInit(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void initAllComponents() {
        // 窗口基础配置
        mainFrame.setTitle("简易文件资源管理器");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(new Dimension(1200, 800));
        mainFrame.setLocationRelativeTo(null);

        // 创建菜单栏
        createMenuBar();
        // 创建工具栏+路径栏+搜索栏
        JPanel northPanel = createNorthPanel();
        // 创建文件树+滚动面板
        JScrollPane treeScroll = createFileTree();
        // 创建文件表格+滚动面板
        JScrollPane tableScroll = createFileTable();
        // 创建预览区
        JScrollPane previewScroll = createPreviewArea();
        // 创建主分割面板
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, tableScroll);
        mainSplit.setDividerLocation(300);
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplit, previewScroll);
        verticalSplit.setDividerLocation(500);
        // 状态栏
        JLabel statusLabel = new JLabel("就绪");

        // 组装主窗口
        Container contentPane = mainFrame.getContentPane();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.add(northPanel, BorderLayout.NORTH);
        contentPane.add(verticalSplit, BorderLayout.CENTER);
        contentPane.add(statusLabel, BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenu editMenu = new JMenu("编辑");
        JMenu viewMenu = new JMenu("视图");
        JMenu toolsMenu = new JMenu("工具");

        JMenuItem copyItem = new JMenuItem("复制");
        JMenuItem cutItem = new JMenuItem("剪切");
        JMenuItem pasteItem = new JMenuItem("粘贴");
        JMenuItem renameItem = new JMenuItem("重命名");
        JMenuItem deleteItem = new JMenuItem("删除");
        JMenuItem exitItem = new JMenuItem("退出");

        JMenuItem findItem = new JMenuItem("查找");
        JMenuItem selectAllItem = new JMenuItem("全选");

        JMenuItem refreshViewItem = new JMenuItem("刷新");
        JCheckBoxMenuItem previewItem = new JCheckBoxMenuItem("显示预览", true);

        JMenuItem statsItem = new JMenuItem("统计文件夹");
        JMenuItem searchFilesItem = new JMenuItem("搜索文件");
        JMenuItem viewLogItem = new JMenuItem("查看操作日志");

        fileMenu.add(copyItem);fileMenu.add(cutItem);fileMenu.add(pasteItem);fileMenu.addSeparator();
        fileMenu.add(renameItem);fileMenu.add(deleteItem);fileMenu.addSeparator();fileMenu.add(exitItem);
        editMenu.add(findItem);editMenu.add(selectAllItem);
        viewMenu.add(refreshViewItem);viewMenu.add(previewItem);
        toolsMenu.add(statsItem);toolsMenu.add(searchFilesItem);toolsMenu.add(viewLogItem);

        menuBar.add(fileMenu);menuBar.add(editMenu);menuBar.add(viewMenu);menuBar.add(toolsMenu);
        mainFrame.setJMenuBar(menuBar);
    }

    private JPanel createNorthPanel() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        mainFrame.backButton = new JButton("←");
        mainFrame.forwardButton = new JButton("→");
        mainFrame.refreshButton = new JButton("刷新");
        toolBar.add(mainFrame.backButton);
        toolBar.add(mainFrame.forwardButton);
        toolBar.addSeparator();
        toolBar.add(mainFrame.refreshButton);

        // 搜索面板
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        mainFrame.searchField = new JTextField();
        mainFrame.searchButton = new JButton("搜索");
        searchPanel.add(new JLabel("搜索:"), BorderLayout.WEST);
        searchPanel.add(mainFrame.searchField, BorderLayout.CENTER);
        searchPanel.add(mainFrame.searchButton, BorderLayout.EAST);

        // 路径栏
        mainFrame.pathField = new JTextField();
        mainFrame.pathField.setEditable(true);

        // 组装北部面板
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        northPanel.add(toolBar, BorderLayout.NORTH);
        northPanel.add(mainFrame.pathField, BorderLayout.CENTER);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        return northPanel;
    }

    private JScrollPane createFileTree() {
        mainFrame.fileTree = new JTree();
        mainFrame.fileTree.setCellRenderer(new FileTreeRenderer());
        JScrollPane treeScroll = new JScrollPane(mainFrame.fileTree);
        treeScroll.setPreferredSize(new Dimension(250, 0));
        return treeScroll;
    }

    private JScrollPane createFileTable() {
        // 修复：正确实例化DefaultTableModel（已导入）
        mainFrame.tableModel = new DefaultTableModel(mainFrame.columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // 修复：DefaultTableModel是TableModel的子类，参数类型匹配
        mainFrame.fileTable = new JTable(mainFrame.tableModel);
        mainFrame.fileTable.setRowHeight(25);
        mainFrame.fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(mainFrame.fileTable);
    }

    private JScrollPane createPreviewArea() {
        mainFrame.previewArea = new JTextArea();
        mainFrame.previewArea.setEditable(false);
        mainFrame.previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane previewScroll = new JScrollPane(mainFrame.previewArea);
        previewScroll.setPreferredSize(new Dimension(0, 200));
        previewScroll.setBorder(BorderFactory.createTitledBorder("文件预览"));
        return previewScroll;
    }
}