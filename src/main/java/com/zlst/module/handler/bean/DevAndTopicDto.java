package com.zlst.module.handler.bean;

/**
 * 设备与topic关联关系
 * @author xiong.jie/170123
 * @create 2019年1月14日
 */
public class DevAndTopicDto {

	private String devId;
	
	private String topicName;
	
	private String operateType;

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
}
