package com.zlst.module.connection.config;

import com.alibaba.fastjson.JSONObject;
import com.zlst.module.connection.enums.ConnTypeEnum;
import com.zlst.module.task.dto.ConnectDto;

/**
 * @author 170137 2018-12-26
 */
public class DevConnCofig  implements Cloneable{

    private static final int DEFAULT_CONN_COUNT = 1;

    private String id;

    private String cltId;

    private String ip;

    private int port;

    private int connCount = 0;

    private int connIndex = 0;

    private int timeout;

    private ConnTypeEnum connType;

    private ConnectDto dto;

    public DevConnCofig(ConnectDto info) {
        this.id = info.getCollectSourceId();
        this.ip = info.getIp();
        this.port = Integer.parseInt(info.getPort());
        this.timeout = info.getTimeOut();
        this.cltId = info.getCollectSourceId();
        JSONObject extObject = info.getExtObject();
        if (null != extObject) {
            this.connCount = extObject.get("connCount") == null ? DEFAULT_CONN_COUNT : extObject.getIntValue("connCount");
        } else {
            this.connCount = DEFAULT_CONN_COUNT;
        }
    }

    public DevConnCofig() { }

    @Override
    public DevConnCofig clone() {
        DevConnCofig config = null;
        try {
            //淺拷貝
            config= (DevConnCofig)super.clone();
        } catch (CloneNotSupportedException e) {
            return new DevConnCofig();
        }
        return config;
    }

    public ConnectDto getDto() {
        return dto;
    }

    public void setDto(ConnectDto dto) {
        this.dto = dto;
    }

    public String getCltId() {
        return cltId;
    }

    public void setCltId(String cltId) {
        this.cltId = cltId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getConnCount() {
        return connCount;
    }

    public void setConnCount(int connCount) {
        this.connCount = connCount;
    }

    public int getConnIndex() {
        return connIndex;
    }

    public void setConnIndex(int connIndex) {
        this.connIndex = connIndex;
    }

    public ConnTypeEnum getConnType() {
        return connType;
    }

    public void setConnType(ConnTypeEnum connType) {
        this.connType = connType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
