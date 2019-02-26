package com.zlst.module.handler.cache;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.zlst.common.config.GlobalCacheManage;
import com.zlst.common.config.GlobalConstants;
import com.zlst.common.redis.RedisUtils;
import com.zlst.module.handler.bean.DataCacheBean;
import com.zlst.module.handler.bean.DataOutputBean;
import com.zlst.module.handler.bean.DevRealDataBean;
import com.zlst.module.handler.bean.QueryDataDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备实时输出缓存
 * @author xiong.jie/170123
 * @create 2019-02-14 18:15
 **/
@Component
public class DevRealDataCache {

    private static final Logger LOG = LoggerFactory.getLogger(DevRealDataCache.class);

    // 历史数据缓存，key->devId:paramCode, value->DataCacheBean
    private static ConcurrentHashMap<String, DataCacheBean> hisDataCache = new ConcurrentHashMap<>();

    // 实时数据缓存，用于批量保存Redis操作， key->devId:paramCode, value->paramValue
    private static ConcurrentHashMap<String, String> realDataCache = new ConcurrentHashMap<>();

    @Autowired
    private RedisUtils redisUtils;

    //定时处理批量缓存业务
  /*  @Scheduled(fixedRate = 5000)
    public void batchCacheTask(){
        long start = System.currentTimeMillis();
        processRealDataCache();
        LOG.info("DevRealDataCache.batchCacheTask, 用时：{}毫秒", (System.currentTimeMillis() - start));
    }*/

    private void processRealDataCache(){
        if (MapUtil.isNotEmpty(realDataCache)) {
            LOG.info("BatchCacheDataThread.run, realDataCache.size:{}", realDataCache.size());
            redisUtils.batchCacheData(realDataCache, GlobalCacheManage.REALDATA_EFFTIME);
            realDataCache.clear();
            LOG.info("BatchCacheDataThread.run, 执行批量数据缓存成功");
        }
    }

    // 缓存设备实时数据
    public void cacheRealData(DataOutputBean dataOutputBean){
        LOG.info("DevRealDataCache.cacheDevRealData, dataOutputBean: {}", dataOutputBean);
        if(dataOutputBean == null){
            LOG.error("DevRealDataCache.cacheDevRealData, dataOutputBean is null");
            return;
        }

        String devId = dataOutputBean.getDevId();
        String cacheKey = devId + GlobalConstants.HTTP_SEPARATOR + dataOutputBean.getParamCode();
        DataCacheBean dataCacheBean = hisDataCache.get(cacheKey);
        long currTime = System.currentTimeMillis();
        if(dataCacheBean != null){
            if(!dataOutputBean.getParamValue().equals(dataCacheBean.getParamValue())){
                dataCacheBean.setParamValue(dataOutputBean.getParamValue());
                dataCacheBean.setValueTime(currTime);
                //更新历史数据缓存
                hisDataCache.put(cacheKey, dataCacheBean);
                //更新实时数据缓存
                realDataCache.put(cacheKey, dataCacheBean.getParamValue());
            }else if(currTime - dataCacheBean.getValueTime() > GlobalCacheManage.REALDATA_EFFTIME_MS){
                dataCacheBean.setValueTime(currTime);
                //更新历史数据缓存
                hisDataCache.put(cacheKey, dataCacheBean);
                //更新实时数据缓存
                realDataCache.put(cacheKey, dataCacheBean.getParamValue());
            }
        }else{
            DataCacheBean dbCache = new DataCacheBean(dataOutputBean.getParamValue(), currTime);
            //插入历史数据缓存
            hisDataCache.put(cacheKey, dbCache);
            //插入实时数据缓存
            realDataCache.put(cacheKey, dataOutputBean.getParamValue());
        }
    }

    /**
     * 根据设备ID批量查询数据
     * @param queryDataDtos, 设备Id集合
     */
    public List<DevRealDataBean> queryDataByDevIds(List<QueryDataDto> queryDataDtos) {
        if(CollUtil.isEmpty(queryDataDtos)){
            LOG.error("RedisUtils.queryDataByDevIds, queryDataDtos is null");
            return null;
        }
        List<DevRealDataBean> realDataList = new ArrayList<>(queryDataDtos.size());
        queryDataDtos.forEach(qyDto -> {
            realDataList.add(getDataByDevId(qyDto.getDevId()));
        });
        LOG.info("RedisUtils.queryDataByDevIds, finish, realDataList: {}", JSON.toJSONString(realDataList));
        return realDataList;
    }

    /**
     * 根据设备ID和设备编码批量查询数据
     * @param queryDataList, 设备Id集合
     */
    public List<DevRealDataBean> queryDataByDevIdAndCodes(List<QueryDataDto> queryDataList) {
        if(CollUtil.isEmpty(queryDataList)){
            LOG.error("RedisUtils.queryDataByDevIdAndCodes, queryDataList is null");
            return null;
        }
        List<DevRealDataBean> realDataList = new ArrayList<>(queryDataList.size());
        Map<String, Set<String>> paramMap = convertDtoToMap(queryDataList);
        if(MapUtil.isNotEmpty(paramMap)){
            for (String devId : paramMap.keySet()){
                realDataList.add(getDataByDevIdAndCodes(devId, paramMap.get(devId)));
            }
        }

        LOG.info("RedisUtils.queryDataByDevIdAndCodes, finish, realDataList: {}", JSON.toJSONString(realDataList));
        return realDataList;
    }

    private DevRealDataBean getDataByDevId(String devId){
        if(StringUtils.isEmpty(devId)){
            return null;
        }

        Map<String, String> devCacheDataMap = redisUtils.getMapFromHash(GlobalCacheManage.PRE_DEV_REALDATA_CACHE + devId);
        if(MapUtil.isEmpty(devCacheDataMap)){
            LOG.info("RedisUtils.getDataByDevId, 缓存数据为空, devId: {}", devId);
            return null;
        }else{
            DevRealDataBean realData = new DevRealDataBean(devId, devCacheDataMap);
            return realData;
        }
    }

    private DevRealDataBean getDataByDevIdAndCodes(String devId, Set<String> codeSet){
        if(StringUtils.isEmpty(devId) || CollUtil.isEmpty(codeSet)){
            return null;
        }

        Map<String, String> devCacheDataMap = redisUtils.getMapFromHash(GlobalCacheManage.PRE_DEV_REALDATA_CACHE + devId, codeSet);
        if(MapUtil.isEmpty(devCacheDataMap)){
            LOG.info("RedisUtils.getDataByDevIdAndCodes, 缓存数据为空，开始调用实时查询接口");
            return null;
        }else{
            DevRealDataBean realData = new DevRealDataBean(devId, devCacheDataMap);
            return realData;
        }
    }

    private Map<String, Set<String>> convertDtoToMap (List<QueryDataDto> queryDataList){
        Map<String, Set<String>> dataParams = new HashMap<String, Set<String>>();
        queryDataList.forEach(queryDto -> {
            Set<String> codeSet = dataParams.get(queryDto.getDevId());
            if(codeSet == null){
                codeSet = new HashSet<>();
                codeSet.add(queryDto.getParamCode());
            }else{
                codeSet.add(queryDto.getParamCode());
            }
            dataParams.put(queryDto.getDevId(), codeSet);
        });
        return dataParams;
    }

}
