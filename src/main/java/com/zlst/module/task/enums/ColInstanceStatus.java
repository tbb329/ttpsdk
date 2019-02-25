package com.zlst.module.task.enums;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 采集任务实例状态
 * @author xiong.jie/170123
 * @create 2018年12月27日
 */
public enum ColInstanceStatus {
    waiting("0", "待执行"), 
    
    executing("1", "执行中"), 
    
    suspend("2", "暂定执行"),
	
	exception("3", "执行异常"),
	
	invalid("9", "失效");
	
	private String code;

    private String desc;

    ColInstanceStatus(String code, String desc){
	     this.code = code;
	     this.desc = desc;
    }
    
    private static final Map<String, String> lookup = new HashMap<>(ColInstanceStatus.values().length);
    
    static {
    	for(ColInstanceStatus temp : EnumSet.allOf(ColInstanceStatus.class)){
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
