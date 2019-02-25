package com.zlst.module.task.dto;

import com.zlst.module.task.enums.ColInstanceStatus;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/1/7
 */
public class DispatchDto {

    private CollectSourceDto colSrcInfoToExeDto;

    private AgreementDto agreementToExeDto;

    private ConnectDto connectToExeDto;

    private SerMemberDto serMemberToExeDto;

    private String instanceStatus;

    public CollectSourceDto getColSrcInfoToExeDto() {
        return colSrcInfoToExeDto;
    }

    public void setColSrcInfoToExeDto(CollectSourceDto colSrcInfoToExeDto) {
        this.colSrcInfoToExeDto = colSrcInfoToExeDto;
    }

    public AgreementDto getAgreementToExeDto() {
        return agreementToExeDto;
    }

    public void setAgreementToExeDto(AgreementDto agreementToExeDto) {
        this.agreementToExeDto = agreementToExeDto;
    }

    public ConnectDto getConnectToExeDto() {
        return connectToExeDto;
    }

    public void setConnectToExeDto(ConnectDto connectToExeDto) {
        this.connectToExeDto = connectToExeDto;
    }

    public SerMemberDto getSerMemberToExeDto() {
        return serMemberToExeDto;
    }

    public void setSerMemberToExeDto(SerMemberDto serMemberToExeDto) {
        this.serMemberToExeDto = serMemberToExeDto;
    }

    public String getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(String instanceStatus) {
        this.instanceStatus = instanceStatus;
    }
}
