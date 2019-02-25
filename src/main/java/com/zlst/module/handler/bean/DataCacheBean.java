package com.zlst.module.handler.bean;

import java.io.Serializable;

/**
 * 数据缓存Bean
 * @author xiong.jie/170123
 * @create 2019-02-20 11:08
 **/
public class DataCacheBean {

    private String paramValue;

    private Long valueTime;

    public DataCacheBean() {
    }

    public DataCacheBean(String paramValue, Long valueTime) {
        this.paramValue = paramValue;
        this.valueTime = valueTime;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Long getValueTime() {
        return valueTime;
    }

    public void setValueTime(Long valueTime) {
        this.valueTime = valueTime;
    }
}
