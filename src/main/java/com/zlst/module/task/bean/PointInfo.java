package com.zlst.module.task.bean;

import java.util.HashMap;

/**
 * 测点bean
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/28
 */
public class PointInfo {

    private HashMap<String, String> param;

    public PointInfo(HashMap<String, String> params){
        this.param = params;
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }
}
