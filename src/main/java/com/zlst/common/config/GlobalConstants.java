package com.zlst.common.config;

import com.zlst.param.ObjectToResult;

/**
 * 全局常量
 * @author xiong.jie/170123
 * @create 2018年12月26日
 */
public final class GlobalConstants {
    
    // 字符编码
    public static final String UTF = "UTF-8";
    
    public static final String GBK = "GBK";
    
    // 统一日期时间格式
    public static final String TIMEZONE = "GMT+08:00";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // =======================请求接口返回值====================
    public static final String RES_SUCC = ObjectToResult.SUCCESS_CODE + "";
    
    public static final String RES_FAIL = ObjectToResult.FAIL_CODE + "";
    // =======================请求接口返回值====================
    
    
    //========================调用uip服务接口====================
    public static final String HTTP_SEPARATOR = ":";
    
    public static final String CALL_UIP_HTTP_PREFIX = "http" + HTTP_SEPARATOR + "//";
    
    public static final String CALL_UIP_DEFAULT_PORT = "80";
    
    // 开发模式
    public static final String CALL_ACT_INVOKE_MODE_DEV = "debug";
    
    // 生产模式
    public static final String CALL_ACT_INVOKE_MODE_PRO = "pro";
    
    // 根据serviceCode查询对应的设备与PLC关系接口 GET请求
    public static final String ACTUATOR_EQUIPANDCS_INFO = "/api/caas/equipment/getAllDevPlcInfo";

    // 查询设备与TOPIC关系接口 GET请求
    public static final String ACTUATOR_DEVTOPIC_INFO = "/api/caas/message/member/equipIdTopic";

    //========================调用调度组件接口====================
    // 任务重连请求接口
    public static final String DIS_PUBLISH_TASK = "/api/caas/dispatchSer/qyMemberBySerCode";

    public static final String[] FILTER_GROUP_ELEMENTS = {"periodTime"};

    public static final String POINT_INFO = "pointInfo";

    public static final String GROUP_NAME = "groupName";

    public static final String PERIOD_TIME = "periodTime";

    public static final String GROUP_INFO = "groupInfo";
    
    
    
    
}
