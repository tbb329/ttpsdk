package com.zlst.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/1/8
 */
@Component
public class SpringUtils implements ApplicationContextAware{

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    public static <T> T getBeanByName(String beanName){
        if(applicationContext.containsBean(beanName)){
            return (T) applicationContext.getBean(beanName);
        }
        return null;
    }

    public static <T> T getBeanByClass(Class clazz){
        return (T)applicationContext.getBean(clazz);
    }

}
