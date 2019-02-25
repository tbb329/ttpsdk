package com.zlst.module.task.dto;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/1/7
 */
public class SerMemberDto {

    private String serviceId;

    private String status;

    private String memberId;

    private String remarks;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
