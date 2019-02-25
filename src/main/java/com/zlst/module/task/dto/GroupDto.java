package com.zlst.module.task.dto;

import com.alibaba.fastjson.JSONObject;
import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.bean.PointInfo;
import com.zlst.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/29
 */
public class GroupDto {

    private String groupName;

    private long periodTime;

    private List<Map<String, String>> pointInfoList;

    private HashMap<String, String> params;

    private String agreementType;

    private String collectId;

    private String collectStatus;

    public GroupDto(){}

    public GroupDto(String groupName, String periodTime, HashMap<String, String> params, AgreementInfo agreementInfo, List<PointInfo> pointInfoList){
        this.groupName = groupName;
        this.periodTime = TimeUtil.strToTime(periodTime);
        this.params = params;
        this.collectId = agreementInfo.getCollectSourceId();
        this.collectStatus = agreementInfo.getCollectStatus();
        this.agreementType = agreementInfo.getAgreementType();
        this.pointInfoList = new ArrayList<>(pointInfoList.size());
        for (PointInfo pointInfo : pointInfoList) {
            this.pointInfoList.add(pointInfo.getParam());
        }
    }

    public List<Map<String, String>> getPointInfoList() {
        return pointInfoList;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(long periodTime) {
        this.periodTime = periodTime;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus) {
        this.collectStatus = collectStatus;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public static void main(String[] args) {
        GroupDto dto = new GroupDto();
        dto.setGroupName("test1");
        dto.setAgreementType("S7_300");
        dto.setCollectId("40288ce567ee857e01671e9427e80008");
        List<Map<String, String>> pointInfoList = new ArrayList<>();
        Map<String, String> one = new HashMap<>();
        one.put("offset", "0");
        one.put("type", "U8");
        one.put("format", "%2.0f");
        pointInfoList.add(one);
        dto.pointInfoList = pointInfoList;
        System.out.println(JSONObject.toJSONString(dto));

    }
}
