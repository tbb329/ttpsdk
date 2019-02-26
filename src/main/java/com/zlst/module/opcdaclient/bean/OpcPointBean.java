package com.zlst.module.opcdaclient.bean;

import org.openscada.opc.lib.da.Item;

/**
 * Created by 170095 on 2019/2/19.
 */
public class OpcPointBean {
    private String pointName;

    private String dataCode;

    private Item pointItem;

    public OpcPointBean(String pointName, String dataCode, Item pointItem){
        this.pointName = pointName;
        this.dataCode = dataCode;
        this.pointItem = pointItem;
    }
    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getDataCode() {
        return dataCode;
    }

    public void setDataCode(String dataCode) {
        this.dataCode = dataCode;
    }

    public Item getPointItem() {
        return pointItem;
    }

    public void setPointItem(Item pointItem) {
        this.pointItem = pointItem;
    }

}
