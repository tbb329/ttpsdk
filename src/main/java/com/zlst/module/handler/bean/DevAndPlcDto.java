package com.zlst.module.handler.bean;

/**
 * 设备与PLC关联Dto
 * @author xiong.jie/170123
 * @create 2019年1月14日
 */
public class DevAndPlcDto {

    private String equipmentId;
    
    private String equipParamCode;
    
    private String collectSourceId;
    
    private String standCode;

    // 0:增加设备与PLC关联关系,1:删除设备与PLC关联关系
    private String operateType;

    public DevAndPlcDto(){
    	
    }
    
    public DevAndPlcDto(String equipmentId, String collectSourceId,String equipParamCode,String standCode){
        this.equipmentId = equipmentId;
        this.collectSourceId = collectSourceId;
        this.equipParamCode = equipParamCode;
        this.standCode =  standCode;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }


    public String getCollectSourceId() {
        return collectSourceId;
    }

    public void setCollectSourceId(String collectSourceId) {
        this.collectSourceId = collectSourceId;

    }
    public String getEquipParamCode() {
        return equipParamCode;
    }

    public void setEquipParamCode(String equipParamCode) {
        this.equipParamCode = equipParamCode;
    }

    public String getStandCode() {
        return standCode;
    }

    public void setStandCode(String standCode) {
        this.standCode = standCode;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
