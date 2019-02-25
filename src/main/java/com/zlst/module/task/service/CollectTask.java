package com.zlst.module.task.service;

import com.zlst.module.connection.bean.CollectTaskBean;
import com.zlst.module.task.dto.GroupDto;
import groovy.lang.GroovyObject;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * 采集队列类
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/28
 */
public class CollectTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CollectTask.class);

    private ByteBuf collectPackage;

    private GroupDto groupDto;

    private long period;

    private long nextTime;

    private long lastTime;

    private long currentTime;

    private GroovyObject ptcObj;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss:SSS");

    public CollectTask(GroupDto groupDto, long period, GroovyObject ptcObj){
        this.groupDto = groupDto;
        this.period = period;
        this.ptcObj = ptcObj;
    }

    @Override
    public void run() {
        try {
//            logger.error("CollectTask working:" + groupDto.getGroupName());
            //TODO 调用连接方法，发送数据包和测点列表
            currentTime = System.currentTimeMillis();
            nextTime = currentTime + period;
            if ((null == collectPackage) || (collectPackage.readableBytes() < 1)) {
                logger.error("collectPackge is null");
//                collectPackage = getGroupCmd(groupDto);
//                collectPackage.writeBytes(msg);
            } else {
                CollectTaskBean taskBean = new CollectTaskBean(groupDto.getCollectId(), groupDto.getGroupName(),
                        period, collectPackage, ptcObj);
//            logger.info(simpleDateFormat.format(new Date()) + " --> start task:{}", taskBean.getGroupName());
//                CollectTaskActuator.sendMsg(taskBean);
            }

//        logger.error("执行连接方法，上次执行任务时间是 {}, 当前执行时间是 {}, 该任务周期为 {}, 下次执行时间为 {}",
//                    lastTime == 0? 0: simpleDateFormat.format(new Date(lastTime)), simpleDateFormat.format(new Date(currentTime)), period+ "ms", simpleDateFormat.format(new Date(nextTime)));
            lastTime = currentTime;
//            System.out.println("currentTime:" + currentTime);
        } catch (Exception e) {
            logger.error("CollectTask error:" + groupDto.getGroupName());
            e.printStackTrace();
        }
    }

//    public ByteBuf getGroupCmd(GroupDto groupDto) {
//        groupDto.getParams().put("groupName", groupDto.getGroupName());
//        GroovyObject ptcObj = this.ptcObj;
//        if (null != ptcObj) {
//            SendDataDto sendInfo = GroovyObjManage.getSendInfo(ptcObj, groupDto.getParams(), groupDto.getPointInfoList());
//            if (null == sendInfo) {
//                return null;
//            }
//            return sendInfo.getData();
//        }
//        return null;
//    }
}
