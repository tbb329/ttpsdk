package com.zlst.module.handler.bean;

/**
 * plc转换为设备的dto
 * @author xiong.jie/170123
 * @create 2019年1月14日
 */
public class PlcToDevDto {

	// 采集源id
	private String colSrcId;
	    
	// 数据编码
	private String dataCode;

	// 数据值
	private String dataValue;
	
	public PlcToDevDto() {
		
	}

	public PlcToDevDto(String colSrcId, String dataCode, String dataValue) {
		super();
		this.colSrcId = colSrcId;
		this.dataCode = dataCode;
		this.dataValue = dataValue;
	}

	public String getColSrcId() {
		return colSrcId;
	}

	public void setColSrcId(String colSrcId) {
		this.colSrcId = colSrcId;
	}

	public String getDataCode() {
		return dataCode;
	}

	public void setDataCode(String dataCode) {
		this.dataCode = dataCode;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
}
