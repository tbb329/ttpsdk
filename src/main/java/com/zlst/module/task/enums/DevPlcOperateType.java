package com.zlst.module.task.enums;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备与
 * @author xiong.jie/170123
 * @create 2019年1月15日
 */
public enum DevPlcOperateType {

    addDevPlc("0", "增加设备与PLC关联关系"),

    delDevPlc("1", "删除设备与PLC关联关系");

	private String code;

    private String desc;

    DevPlcOperateType(String code, String desc){
	     this.code = code;
	     this.desc = desc;
    }
    
    private static final Map<String, String> lookup = new HashMap<>(DevPlcOperateType.values().length);
    
    static {
    	for(DevPlcOperateType temp : EnumSet.allOf(DevPlcOperateType.class)){
            lookup.put(temp.getCode(), temp.getDesc());
        }
    }

    public static String findDesc(String code){
    	return lookup.get(code);
    }

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static Map<String, String> getLookup() {
		return lookup;
	}
}
