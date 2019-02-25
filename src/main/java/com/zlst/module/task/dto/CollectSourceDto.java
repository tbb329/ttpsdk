package com.zlst.module.task.dto;

/**
 * 由调度组件传输数据bean
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/28
 */
public class CollectSourceDto {

    private String collectSourceId;

    private String collectSourceName;

    private String collectSourceCode;

    private String collectSourceType;

    private String factoryId;

    private String status;

    public String getCollectSourceId() {
        return collectSourceId;
    }

    public void setCollectSourceId(String collectSourceId) {
        this.collectSourceId = collectSourceId;
    }

    public String getCollectSourceName() {
        return collectSourceName;
    }

    public void setCollectSourceName(String collectSourceName) {
        this.collectSourceName = collectSourceName;
    }

    public String getCollectSourceCode() {
        return collectSourceCode;
    }

    public void setCollectSourceCode(String collectSourceCode) {
        this.collectSourceCode = collectSourceCode;
    }

    public String getCollectSourceType() {
        return collectSourceType;
    }

    public void setCollectSourceType(String collectSourceType) {
        this.collectSourceType = collectSourceType;
    }

    public String getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(String factoryId) {
        this.factoryId = factoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
