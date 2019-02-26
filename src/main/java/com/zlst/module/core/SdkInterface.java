package com.zlst.module.core;

import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.dto.ConnectDto;
import com.zlst.module.task.dto.GroupDto;

import java.util.List;

/**
 * Created by 170095 on 2019/2/19.
 */
public interface SdkInterface extends Runnable {

 /**
     * 创建连接
     * @return
     */

//    void connect(ConnectDto connectToExeDto, AgreementInfo agreementInfo, List<GroupDto> groupDtoList);

    void setParam(ConnectDto connectToExeDto, AgreementInfo agreementInfo, List<GroupDto> groupDtoList, String threadName);

    /**
     * 移除连接
     * @return
     */
    void disconnect();

    /**
     * 发送数据
     * @return 发送成功返回 true
     */
    public boolean send();

    /**
     * 连接状态
     * @return
     */
    boolean getStatus();




}
