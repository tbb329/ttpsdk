package com.zlst.utils;

import com.zlst.common.redis.RedisUtils;
import com.zlst.common.spring.SpringUtil;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2018/12/28
 */
public class CusStringUtil {

    public static String padLeft(int width, long sourceCode){
        String seq = String.valueOf(sourceCode);
        int size = seq.length();
        if(size == width){
            return seq;
        } else if(size > width){
            return seq.substring(0, width);
        }
        int difLength = width - size;
        StringBuilder newSeq = new StringBuilder();
        for (int i = 0; i < difLength; i++) {
            newSeq.append("0");
        }
        newSeq.append(seq);
        return newSeq.toString();
    }
}
