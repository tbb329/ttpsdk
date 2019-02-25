package com.zlst.module.handler.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * 设备按实时数据bean
 * @author xiong.jie/170123
 * @create 2019-02-15 11:03
 **/
public class DevRealDataBean implements Serializable {

    private String devId;

    //key->devCode, value->paramValue
    Map<String, String> devDataMap;

    public DevRealDataBean() {
    }

    public DevRealDataBean(String devId, Map<String, String> devDataMap) {
        this.devId = devId;
        this.devDataMap = devDataMap;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public Map<String, String> getDevDataMap() {
        return devDataMap;
    }

    public void setDevDataMap(Map<String, String> devDataMap) {
        this.devDataMap = devDataMap;
    }
}
