package com.zlst.module.connection.bean;

import com.zlst.module.connection.enums.SendStatusEnum;
import groovy.lang.GroovyObject;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;

/**
 * 采集任务实例
 * @author 170137 2019-01-17
 */
public class CollectTaskBean {

    /**
     * 采集源ID
     */
    private String cltId;

    /**
     * 分组name
     */
    private String groupName;

    /**
     * 连接id
     */
    private String connId;

    /**
     * 任务超时时间(既任务执行周期)
     */
    private int timeOut;


    /**
     * 发送内容
     */
    private ByteBuf sendMsg;

    /**
     * 发送时间
     */
    private long sendTime;

    /**
     * 接收内容
     */
    private ByteBuf recvMsg;

    /**
     * 接收时间
     */
    private long recvTime;

    /**
     * 完成时间
     */
    private long completeTime;

    /**
     * 协议对象
     */
    private GroovyObject ptcObj;

    /**
     * 任务状态
     */
    private SendStatusEnum status;

    /**
     * 还需读取多少字节数据
     */
    private int needLen;

    public CollectTaskBean(String cltId, String groupName, long timeOut, ByteBuf sendMsg, GroovyObject ptcObj) {
        this.cltId = cltId;
        this.groupName = groupName;
        this.timeOut = (int)timeOut;
        this.sendMsg = sendMsg;
        this.status = SendStatusEnum.READ;
        this.ptcObj = ptcObj;
        this.sendTime = System.currentTimeMillis();
    }

    public void destory(){
        if ((null != this.recvMsg) && (this.recvMsg.refCnt() != 0)) {
            ReferenceCountUtil.release(this.recvMsg);
        }
        if ((null != this.sendMsg) && (this.sendMsg.refCnt() != 0)) {
            ReferenceCountUtil.release(this.sendMsg);
        }
    }

    public String getCltId() {
        return cltId;
    }

    public void setCltId(String cltId) {
        this.cltId = cltId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getConnId() {
        return connId;
    }

    public void setConnId(String connId) {
        this.connId = connId;
    }

    public ByteBuf getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(ByteBuf sendMsg) {
        this.sendMsg = sendMsg;
    }

    public ByteBuf getRecvMsg() {
        return recvMsg;
    }

    public void setRecvMsg(ByteBuf recvMsg) {
        this.recvMsg = recvMsg;
    }

    public SendStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SendStatusEnum status) {
        this.status = status;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getRecvTime() {
        return recvTime;
    }

    public void setRecvTime(long recvTime) {
        this.recvTime = recvTime;
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public int getNeedLen() {
        return needLen;
    }

    public void setNeedLen(int needLen) {
        this.needLen = needLen;
    }

    public GroovyObject getPtcObj() {
        return ptcObj;
    }

    public void setPtcObj(GroovyObject ptcObj) {
        this.ptcObj = ptcObj;
    }
}
