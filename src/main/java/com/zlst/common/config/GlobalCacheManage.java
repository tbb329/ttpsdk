package com.zlst.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 缓存管理
 */
@Component
public class GlobalCacheManage {

    public static final String PRE_ACTUATOR_CACHE = "actuator:";

    public static final String PRE_DEV_REALDATA_CACHE = PRE_ACTUATOR_CACHE + "realdata:";

    public static final long REALDATA_EFFTIME = 60; //实时数据有效时间,单位:秒,超过这个时间未更新的数据,会被丢掉

    public static final long REALDATA_EFFTIME_MS = REALDATA_EFFTIME * 1000; //实时数据有效时间，单位：毫秒

    public static final String PRE_LOCK = PRE_ACTUATOR_CACHE + "lock:";

    // 任务重连请求接口
    public static final String DIS_PUBLISH_TASK = "/api/caas/dispatchSer/qyMemberBySerCode";

    public static final String[] FILTER_GROUP_ELEMENTS = {"periodTime"};

    public static final String POINT_INFO = "pointInfo";

    public static final String GROUP_NAME = "groupName";

    public static final String PERIOD_TIME = "periodTime";

    public static final String GROUP_INFO = "groupInfo";
}
