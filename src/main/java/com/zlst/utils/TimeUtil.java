package com.zlst.utils;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/29
 */
public class TimeUtil {

    //字符串时间统一转为以毫秒为单位
    public static long strToTime(String strTime){
        if(strTime.contains("h")){
            long hour = Long.valueOf(strTime.split("h")[0]);
            return hour * 60 * 60 * 1000;
        }else if(strTime.contains("min")){
            long min = Long.valueOf(strTime.split("min")[0]);
            return min * 60 * 1000;
        }else if(strTime.contains("ms")){
            return Long.valueOf(strTime.split("ms")[0]);
        }else if(strTime.contains("s")){
            long sec = Long.valueOf(strTime.split("s")[0]);
            return sec * 1000;
        }
        return Long.valueOf(strTime);
    }
}
