package com.zlst.module.handler.bean;

import java.io.Serializable;

/**
 * 查询缓存数据Dto
 * @author xiong.jie/170123
 * @create 2019-02-22 14:22
 **/
public class QueryDataDto implements Serializable {

    private String devId;

    private String paramCode;

    public QueryDataDto() {
    }

    public QueryDataDto(String devId, String paramCode) {
        this.devId = devId;
        this.paramCode = paramCode;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }
}
