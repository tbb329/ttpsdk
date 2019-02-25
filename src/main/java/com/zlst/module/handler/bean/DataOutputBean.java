package com.zlst.module.handler.bean;

import cn.hutool.core.util.StrUtil;

import java.util.Date;

/**
 * 数据输出对象
 * @author xiong.jie/170123
 * @create 2019年1月10日
 */
public class DataOutputBean {

	private String devId;
	
	private String paramCode;
	
	private String paramValue;
	
	private Date nowDate;
	
	public DataOutputBean() {
		super();
	}
	
	public DataOutputBean(String devId, String paramCode, String paramValue, Date nowDate) {
		super();
		this.devId = devId;
		this.paramCode = paramCode;
		this.paramValue = paramValue;
		this.nowDate = nowDate;
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

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public Date getNowDate() {
		return nowDate;
	}

	public void setNowDate(Date nowDate) {
		this.nowDate = nowDate;
	}

	@Override
	public String toString() {
		return String.format("DataOutputBean{devId=%s, paramCode=%s, paramValue=%s, nowDate=%s}", devId, paramCode, paramValue, nowDate);
	}

}
