package com.zlst.module.task.utils;

import cn.hutool.core.collection.IterUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zlst.common.config.GlobalCacheManage;
import com.zlst.common.config.GlobalConstants;
import com.zlst.module.task.bean.AgreementInfo;
import com.zlst.module.task.bean.PointInfo;
import com.zlst.module.task.dto.GroupDto;
import org.slf4j.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @Author wangqiyuan
 * Created by 170253 on 2019/2/19
 */
public class AgreementAnalyzeUtil {

    private static final Logger logger = LoggerFactory.getLogger(AgreementAnalyzeUtil.class);

    public static List<GroupDto> parseAgreement(AgreementInfo agreementInfo){
        logger.info("开始执行解析协议信息!!!!");
        Map<String, String> agreementParam = jsonObjectToHash(JSONObject.parseObject(agreementInfo.getAgreementContent()));
        String groupInfo = agreementParam.get(GlobalConstants.GROUP_INFO);
        agreementParam.remove(GlobalConstants.GROUP_INFO);
        agreementInfo.setAgreementMap(agreementParam);
        agreementInfo.setAgreementContent(groupInfo);
        return parseToGroupDto(agreementInfo);
    }

    public static List<GroupDto> parseToGroupDto(AgreementInfo agreementInfo){
        logger.info("开始解析分组信息!!!!");
        JSONArray groupArray = JSONArray.parseArray(agreementInfo.getAgreementContent());

        List<GroupDto> groupDtoList = groupArray.stream().map( group -> {
            JSONObject groupObject = (JSONObject) JSONObject.toJSON(group);
            GroupDto groupDto = jsonObjectToGroup(groupObject, agreementInfo);
            return groupDto;
        }).collect(toList());

        return groupDtoList;
    }

    public static GroupDto jsonObjectToGroup(JSONObject groupObject, AgreementInfo agreementInfo){
        String groupName = groupObject.get(GlobalConstants.GROUP_NAME).toString();
        String periodTime = groupObject.get(GlobalConstants.PERIOD_TIME).toString();
        HashMap<String, String> params = jsonObjectToHash(groupObject, GlobalConstants.FILTER_GROUP_ELEMENTS);
        List<PointInfo> pointInfoList = strToPoint(params.get(GlobalConstants.POINT_INFO));
        params.remove(GlobalConstants.POINT_INFO);
        logger.info("解析分组 :{} 信息结束", groupName);
        return new GroupDto(groupName, periodTime, params, agreementInfo, pointInfoList);
    }

    public static List<PointInfo> strToPoint(String pointStr){
        JSONArray pointArray = JSONArray.parseArray(pointStr);
        List<PointInfo> pointInfoList = pointArray.stream().map( point -> {
            JSONObject pointObject = (JSONObject) JSONObject.toJSON(point);

            HashMap<String, String> pointParam = jsonObjectToHash(pointObject);
            PointInfo pointInfo = new PointInfo(pointParam);
            return pointInfo;
        }).collect(toList());
        logger.info("解析测点信息结束!!!");
        return pointInfoList;
    }

    public static HashMap<String, String> jsonObjectToHash(JSONObject jsonObject, String... filter){
        HashMap<String, String> params = new HashMap<>(8);
        List<String> filterElements = Arrays.asList(filter);

        for(Iterator<?> iterator = jsonObject.keySet().iterator(); iterator.hasNext();){
            String key = iterator.next().toString();
            if(!IterUtil.isEmpty(filterElements) && filterElements.contains(key)){
                continue;
            }
            params.put(key, jsonObject.get(key).toString());
        }
        return params;
    }

}
