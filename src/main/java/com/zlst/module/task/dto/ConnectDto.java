package com.zlst.module.task.dto;

import com.alibaba.fastjson.JSONObject;

/**
 * 连接信息bean
 * @Author wangqiyuan
 * Created by 170253 on 2018/10/16.
 */

public class ConnectDto {

    private String connectId;

    private String collectSourceId;

    private String connectType;

    private String ip;

    private String port;

    private int timeOut;

    private String baudRate;

    private int stopBit;

    private int checkBit;

    private int dataBit;

    private String extContent;

    private JSONObject extObject;

    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }

    public String getCollectSourceId() {
        return collectSourceId;
    }

    public void setCollectSourceId(String collectSourceId) {
        this.collectSourceId = collectSourceId;
    }

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(String baudRate) {
        this.baudRate = baudRate;
    }

    public int getStopBit() {
        return stopBit;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }

    public int getCheckBit() {
        return checkBit;
    }

    public void setCheckBit(int checkBit) {
        this.checkBit = checkBit;
    }

    public int getDataBit() {
        return dataBit;
    }

    public void setDataBit(int dataBit) {
        this.dataBit = dataBit;
    }

    public String getExtContent() {
        return extContent;
    }

    public void setExtContent(String extContent) {
        this.extContent = extContent;
        this.extObject = JSONObject.parseObject(extContent);
    }

    public JSONObject getExtObject() {
        return extObject;
    }

    public void setExtObject(JSONObject extObject) {
        this.extObject = extObject;
    }
}
