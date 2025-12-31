package com.FileManager.gui;

import com.FileManager.FileOperationLogDAO;
import com.FileManager.OperationLog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 独立抽离：原FileManagerGUI的内部类OperationLogWindow
 * 完整保留原有日志查看/清空功能
 */
public class OperationLogWindow extends JFrame {
    private JTable logTable;
    private DefaultTableModel logTableModel;
    private FileOperationLogDAO logDAO;
    private final FileManagerGUI mainFrame;

    public OperationLogWindow(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
        this.logDAO = new FileOperationLogDAO();
        initLogWindow();
        loadLogData();
    }

    private void initLogWindow() {
        setTitle("操作日志查看");
        setSize(800, 500);
        setLocationRelativeTo(mainFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = {"日志ID", "操作类型", "文件路径", "操作时间", "操作结果", "详情"};
        logTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logTable = new JTable(logTableModel);
        logTable.setRowHeight(25);
        logTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        logTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        logTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        logTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        logTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        logTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("刷新日志");
        JButton clearButton = new JButton("清空日志");
        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        refreshButton.addActionListener(e -> loadLogData());
        clearButton.addActionListener(e -> clearLogs());

        JScrollPane scrollPane = new JScrollPane(logTable);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLogData() {
        logTableModel.setRowCount(0);
        SwingWorker<List<OperationLog>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OperationLog> doInBackground() {
                return logDAO.getAllOperationLogs();
            }
            @Override
            protected void done() {
                try {
                    List<OperationLog> logList = get();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (OperationLog log : logList) {
                        Object[] rowData = {
                                log.getId(), log.getOperationType(), log.getFilePath(),
                                sdf.format(log.getOperationTime()), log.getResult(), log.getDetail()
                        };
                        logTableModel.addRow(rowData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(OperationLogWindow.this, "加载日志失败！");
                }
            }
        };
        worker.execute();
    }

    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要清空所有操作日志吗？", "确认清空", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return logDAO.clearAllLogs();
                }
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(OperationLogWindow.this, "日志清空成功！");
                            loadLogData();
                        } else {
                            JOptionPane.showMessageDialog(OperationLogWindow.this, "日志清空失败！");
                        }
                    } catch (Exception e) {e.printStackTrace();}
                }
            };
            worker.execute();
        }
    }
}