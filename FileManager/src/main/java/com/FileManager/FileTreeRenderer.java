package com.FileManager;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

public class FileTreeRenderer extends DefaultTreeCellRenderer {
    private Icon folderIcon;
    private Icon fileIcon;
    private Icon textFileIcon;
    private Icon javaFileIcon;
    private Icon imageIcon;
    private Icon pdfIcon;

    public FileTreeRenderer() {
        // 使用自定义图标，避免系统图标
        folderIcon = ImageLoader.createFolderIcon(new Color(255, 215, 0)); // 金色
        fileIcon = ImageLoader.createFileIcon(new Color(169, 169, 169)); // 灰色
        textFileIcon = ImageLoader.createTextFileIcon();
        javaFileIcon = ImageLoader.createJavaFileIcon();
        imageIcon = ImageLoader.createImageIcon();
        pdfIcon = ImageLoader.createPDFIcon();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof javax.swing.tree.DefaultMutableTreeNode) {
            javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                File file = (File) userObject;

                if (file.isDirectory()) {
                    setIcon(folderIcon);
                } else {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".txt") || name.endsWith(".ini") || name.endsWith(".bat") ||
                            name.endsWith(".properties")) {
                        setIcon(textFileIcon);
                    } else if (name.endsWith(".java")) {
                        setIcon(javaFileIcon);
                    } else if (name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif") ||
                            name.endsWith(".bmp")) {
                        setIcon(imageIcon);
                    } else if (name.endsWith(".pdf")) {
                        setIcon(pdfIcon);
                    } else {
                        setIcon(fileIcon);
                    }
                }
                if (!file.isDirectory()) {
                    setText(file.getName() + " (" + FileOperations.getFileSize(file) + ")");
                } else {
                    setText(file.getName());
                }
            } else if (userObject instanceof String) {
                setText(userObject.toString());
            }
        }
        return this;
    }
}