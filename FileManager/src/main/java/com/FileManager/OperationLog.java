package com.FileManager;

import java.sql.Timestamp;

/**
 * 操作日志实体类，映射数据库file_operation_log表
 */
public class OperationLog {
    private int id; // 日志ID
    private String operationType; // 操作类型：复制/剪切/粘贴/重命名/删除
    private String filePath; // 操作的文件路径
    private Timestamp operationTime; // 操作时间
    private String result; // 操作结果：成功/失败
    private String detail; // 操作详情

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Timestamp operationTime) {
        this.operationTime = operationTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}