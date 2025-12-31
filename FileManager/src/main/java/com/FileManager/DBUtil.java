package com.FileManager;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具类
 */
public class DBUtil {
    // 数据库连接参数
    private static final String URL_WITHOUT_DB = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String URL = "jdbc:mysql://localhost:3306/file_manager_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // MySQL用户名
    private static final String PASSWORD = "root"; // MySQL密码

    static {
        // 加载MySQL驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行本地SQL脚本文件初始化数据库
     * @param sqlFilePath SQL脚本文件的路径（如"file_manager_db.sql"）
     */
    public static void initDatabaseFromSqlFile(String sqlFilePath) {
        Connection conn = null;
        Statement stmt = null;
        BufferedReader reader = null;

        try {
            // 1. 连接MySQL服务器（不指定具体数据库）
            conn = DriverManager.getConnection(URL_WITHOUT_DB, USER, PASSWORD);
            stmt = conn.createStatement();
            System.out.println("成功连接MySQL服务器，开始执行SQL脚本...");

            // 2. 读取本地SQL文件
            reader = new BufferedReader(new FileReader(sqlFilePath));
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释和空行
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line);
                // 以分号分割SQL语句
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().replace(";", ""); // 移除分号避免语法错误
                    stmt.executeUpdate(sql);
                    sqlBuilder.setLength(0); // 清空缓冲区
                    System.out.println("执行SQL成功：" + sql);
                }
            }

            JOptionPane.showMessageDialog(null, "数据库初始化成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "数据库初始化失败！请检查MySQL服务和SQL脚本。\n错误：" + e.getMessage(),
                    "数据库错误", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "读取SQL文件失败！请检查文件路径：" + sqlFilePath,
                    "文件错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            // 关闭资源
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            close(conn, stmt); // 调用新增的close重载方法
        }
    }

    /**
     * 获取数据库连接
     * @return Connection对象
     * @throws SQLException 数据库连接异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 关闭数据库资源
     * @param conn 连接对象
     * @param pstmt 预处理语句对象
     * @param rs 结果集对象
     */
    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载：关闭连接和预处理语句（无结果集时）
     * @param conn 连接对象
     * @param pstmt 预处理语句对象
     */
    public static void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

    /**
     * 新增重载：关闭Connection和Statement（执行SQL脚本时用）
     */
    public static void close(Connection conn, Statement stmt) {
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}