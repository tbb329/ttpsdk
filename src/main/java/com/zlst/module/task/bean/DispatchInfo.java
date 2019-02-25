package com.zlst.module.task.bean;

import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.GroupDto;

import java.util.List;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/1/23
 */
public class DispatchInfo {

    private AgreementInfo agreementInfo;

    private ConnectDto connectDto;

    private List<GroupDto> groupDtoList;

    public DispatchInfo(){}

    public DispatchInfo(AgreementInfo agreementInfo, ConnectDto connectDto, List<GroupDto> groupDtoList){
        this.agreementInfo = agreementInfo;
        this.connectDto = connectDto;
        this.groupDtoList = groupDtoList;
    }

    public AgreementInfo getAgreementInfo() {
        return agreementInfo;
    }

    public void setAgreementInfo(AgreementInfo agreementInfo) {
        this.agreementInfo = agreementInfo;
    }

    public ConnectDto getConnectDto() {
        return connectDto;
    }

    public void setConnectDto(ConnectDto connectDto) {
        this.connectDto = connectDto;
    }

    public List<GroupDto> getGroupDtoList() {
        return groupDtoList;
    }

    public void setGroupDtoList(List<GroupDto> groupDtoList) {
        this.groupDtoList = groupDtoList;
    }
}
