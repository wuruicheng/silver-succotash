package com.FileManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作日志数据访问对象
 */
public class FileOperationLogDAO {
    /**
     * 添加操作日志
     * @param operationType 操作类型（复制/剪切/粘贴/重命名/删除）
     * @param filePath 操作的文件路径
     * @param result 操作结果（成功/失败）
     * @param detail 操作详情
     * @return 是否添加成功
     */
    public boolean addOperationLog(String operationType, String filePath, String result, String detail) {
        String sql = "INSERT INTO file_operation_log(operation_type, file_path, operation_time, result, detail) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, operationType);
            pstmt.setString(2, filePath);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, result);
            pstmt.setString(5, detail == null ? "" : detail);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }

    /**
     * 查询所有操作日志（按时间倒序）
     * @return 日志列表
     */
    public List<OperationLog> getAllOperationLogs() {
        String sql = "SELECT id, operation_type, file_path, operation_time, result, detail FROM file_operation_log ORDER BY operation_time DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OperationLog> logList = new ArrayList<>();

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OperationLog log = new OperationLog();
                log.setId(rs.getInt("id"));
                log.setOperationType(rs.getString("operation_type"));
                log.setFilePath(rs.getString("file_path"));
                log.setOperationTime(rs.getTimestamp("operation_time"));
                log.setResult(rs.getString("result"));
                log.setDetail(rs.getString("detail"));
                logList.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(conn, pstmt, rs);
        }
        return logList;
    }

    /**
     * 清空所有操作日志
     * @return 是否清空成功
     */
    public boolean clearAllLogs() {
        String sql = "DELETE FROM file_operation_log";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.close(conn, pstmt);
        }
    }
}