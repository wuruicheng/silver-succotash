package com.FileManager.handler;

import com.FileManager.gui.FileManagerGUI;
import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.io.File;
import java.util.List;

/**
 * 负责：文件树的所有逻辑
 * 包含：树加载、延迟加载子节点、选中目录切换
 */
public class FileTreeHandler {
    private final FileManagerGUI mainFrame;

    public FileTreeHandler(FileManagerGUI mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void loadFileTree() {
        File[] roots = File.listRoots();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("我的电脑");

        for (File root : roots) {
            DefaultMutableTreeNode driveNode = new DefaultMutableTreeNode(root);
            rootNode.add(driveNode);
            driveNode.add(new DefaultMutableTreeNode("正在加载..."));
        }

        mainFrame.treeModel = new DefaultTreeModel(rootNode);
        mainFrame.fileTree.setModel(mainFrame.treeModel);

        mainFrame.fileTree.addTreeWillExpandListener(new TreeWillExpandListener() {
            @Override
            public void treeWillExpand(TreeExpansionEvent event) {
                TreePath path = event.getPath();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getChildCount() == 1) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getFirstChild();
                    if ("正在加载...".equals(child.getUserObject())) {
                        loadTreeChildNodes(node);
                    }
                }
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) {}
        });
    }

    private void loadTreeChildNodes(DefaultMutableTreeNode parentNode) {
        SwingWorker<Void, DefaultMutableTreeNode> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                Object obj = parentNode.getUserObject();
                if (obj instanceof File) {
                    File dir = (File) obj;
                    File[] files = dir.listFiles(File::isDirectory);
                    if (files != null) {
                        parentNode.removeAllChildren();
                        for (File file : files) {
                            publish(new DefaultMutableTreeNode(file));
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<DefaultMutableTreeNode> chunks) {
                for (DefaultMutableTreeNode node : chunks) {
                    parentNode.add(node);
                    node.add(new DefaultMutableTreeNode("正在加载..."));
                }
                ((DefaultTreeModel) mainFrame.treeModel).reload(parentNode);
            }
        };
        worker.execute();
    }

    public void updateTreeSelection(File directory) {
        DefaultTreeModel treeModel = (DefaultTreeModel) mainFrame.treeModel;
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        findAndSelectNode(root, directory);
    }

    public boolean findAndSelectNode(DefaultMutableTreeNode node, File targetFile) {
        Object obj = node.getUserObject();
        if (obj instanceof File) {
            File nodeFile = (File) obj;
            if (nodeFile.equals(targetFile)) {
                mainFrame.fileTree.setSelectionPath(new TreePath(node.getPath()));
                mainFrame.fileTree.scrollPathToVisible(new TreePath(node.getPath()));
                return true;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                if (findAndSelectNode(child, targetFile)) {
                    return true;
                }
            }
        }
        return false;
    }
}