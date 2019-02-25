package com.zlst.module.task.bean;

import com.zlst.module.task.dto.AgreementDto;
import com.zlst.module.task.dto.CollectSourceDto;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/1/7
 */
public class AgreementInfo {

    private String agreementId;

    private String agreementType;

    private String agreementTypeName;

    private String agreementContent;

    private String collectSourceId;

    private String collectStatus;

    private Map<String, String> agreementMap;

    public AgreementInfo(AgreementDto agreementDto, CollectSourceDto collectSourceDto){
        BeanUtils.copyProperties(agreementDto, this);
        this.collectSourceId = collectSourceDto.getCollectSourceId();
        this.collectStatus = collectSourceDto.getStatus();
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public String getCollectSourceId() {
        return collectSourceId;
    }

    public void setCollectSourceId(String collectSourceId) {
        this.collectSourceId = collectSourceId;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getAgreementContent() {
        return agreementContent;
    }

    public void setAgreementContent(String agreementContent) {
        this.agreementContent = agreementContent;
    }

    public String getCollectStatus() {
        return collectStatus;
    }

    public void setCollectStatus(String collectStatus) {
        this.collectStatus = collectStatus;
    }

    public Map<String, String> getAgreementMap() {
        return agreementMap;
    }

    public void setAgreementMap(Map<String, String> agreementMap) {
        this.agreementMap = agreementMap;
    }

    public String getAgreementTypeName() {
        return agreementTypeName;
    }

    public void setAgreementTypeName(String agreementTypeName) {
        this.agreementTypeName = agreementTypeName;
    }
}
