package com.zlst.module.protocol.dto;

import io.netty.buffer.ByteBuf;

/**
 *
 * 与设备通讯数据 传输对象
 * @author 170137 2019-01-07
 *
 */
public class SendDataDto {

    String name;

    ByteBuf data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }
}
