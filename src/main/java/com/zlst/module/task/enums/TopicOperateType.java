package com.zlst.module.task.enums;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * topic与设备关联操作类型
 * @author xiong.jie/170123
 * @create 2019年1月15日
 */
public enum TopicOperateType {
	
    addTopic("0", "增加消息主题"), 
    
    delTopic("1", "删除消息主题"), 
    
    addTopicDev("2", "增加消息主题与设备关联关系"),
	
    delTopicDev("3", "删除消息主题与设备关联关系");
	
	private String code;

    private String desc;

    TopicOperateType(String code, String desc){
	     this.code = code;
	     this.desc = desc;
    }
    
    private static final Map<String, String> lookup = new HashMap<>(TopicOperateType.values().length);
    
    static {
    	for(TopicOperateType temp : EnumSet.allOf(TopicOperateType.class)){
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
