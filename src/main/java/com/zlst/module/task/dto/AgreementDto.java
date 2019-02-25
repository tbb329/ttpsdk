package com.zlst.module.task.dto;


import com.zlst.module.task.dto.CollectSourceDto;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 协议信息bean
 * @Author wangqiyuan
 * Created by 170253 on 2018/10/16.
 */

public class AgreementDto {

    private String agreementId;

    private String agreementType;

    private String agreementContent;

    private String agreementTypeName;

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
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

    public String getAgreementTypeName() {
        return agreementTypeName;
    }

    public void setAgreementTypeName(String agreementTypeName) {
        this.agreementTypeName = agreementTypeName;
    }
}
