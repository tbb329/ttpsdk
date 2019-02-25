package com.zlst.module.handler;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;


/**
 * 数据输出类型
 * @author xiong.jie/170123
 * @create 2018年12月27日
 */
public enum DataOutputType {
	
    kafka("0", "kafka类"), 
    
    file("1", "文件类");
	
	private String code;

    private String desc;

    DataOutputType(String code, String desc){
	     this.code = code;
	     this.desc = desc;
    }
    
    private static final Map<String, String> lookup = new HashMap<>(DataOutputType.values().length);
    
    static {
    	for(DataOutputType temp : EnumSet.allOf(DataOutputType.class)){
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