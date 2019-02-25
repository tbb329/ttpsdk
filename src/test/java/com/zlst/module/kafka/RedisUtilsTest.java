package com.zlst.module.kafka;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zlst.ZlstApplication;
import com.zlst.common.config.GlobalCacheManage;
import com.zlst.common.redis.RedisUtils;
import com.zlst.module.handler.bean.DataCacheBean;
import com.zlst.module.handler.bean.DataOutputBean;
import com.zlst.module.handler.bean.DevRealDataBean;
import com.zlst.module.handler.bean.QueryDataDto;
import com.zlst.module.handler.cache.DevRealDataCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类测试
 * @author xiong.jie/170123
 * @create 2019-02-14 18:54
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZlstApplication.class)
public class RedisUtilsTest {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DevRealDataCache devRealDataCache;

    @Test
    public void testCacheBean(){
        //String devId, String paramCode, String paramValue, Date nowDate
        String cachePrefix = "zl:test:";
        Date currentDate = new Date();
        DataOutputBean db1 = new DataOutputBean("dev001", "DY", "101", currentDate);
        DataOutputBean db2 = new DataOutputBean("dev001", "SW", "201", currentDate);
//        DataOutputBean db3 = new DataOutputBean("dev001", "YL", "300", currentDate);
//        DataOutputBean db4 = new DataOutputBean("dev001", "QL", "3400", currentDate);

        Map<String, String> realDataMap = new HashMap();
        realDataMap.put(db1.getParamCode(), JSONObject.toJSONString(db1));
        realDataMap.put(db2.getParamCode(), JSONObject.toJSONString(db2));
//        realDataMap.put(db3.getParamCode(), JSONObject.toJSONString(db3));
//        realDataMap.put(db4.getParamCode(), JSONObject.toJSONString(db4));

        redisUtils.setObjToHash(cachePrefix + db1.getDevId(), realDataMap);
    }

    //3957, 13457
    @Test
    public void testCacheHash(){
        List<DataOutputBean> dataOutBeanList = new ArrayList<>();
        String cacheKey = "xj:test:hashTemp:";
        long startTime = System.currentTimeMillis();
        for (int i=0; i<1000; i++){
            DataOutputBean db = new DataOutputBean("tempDev001" + i, "DY", "101" + i, new Date());
            redisUtils.setFieldValueFromHash(cacheKey + db.getDevId(), db.getParamCode(), JSONObject.toJSONString(db));
            redisUtils.setCacheExpireTime(cacheKey + db.getDevId(), 60, TimeUnit.SECONDS);
        }
//        redisUtils.setCacheExpireTime(cacheKey, 60, TimeUnit.SECONDS);
        System.err.println("dataOutBeanList.size: " + dataOutBeanList.size());
//        redisUtils.batchCacheDevInfo(dataOutBeanList, 300);
        System.err.println("use time: " + (System.currentTimeMillis() - startTime));
    }

    /**
     * 批量插入1000个点位场景，结果：
     *  user1, use time: 8696
     user2, use time: 2426
     user3, use time: 96
     */
    @Test
    public void testBachHash(){
        long start = System.currentTimeMillis();
        String user1Key = "user1";
        for (int i = 0; i < 1000; i++) {
            redisTemplate.opsForHash().put(user1Key, "status" + i, "value" + i);
        }
        redisTemplate.expire(user1Key, 120, TimeUnit.SECONDS);
        System.out.println("user1, use time: "+ (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        String user2Key = "user2";
        final byte[] rawKey = getKeySerializeByte(user2Key);
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                for (int i = 0; i < 1000; i++) {
                    final byte[] rawHashKey = getKeySerializeByte("status" + i);
                    final byte[] rawHashValue = getValueSerializeByte("value" + i);
                    connection.hSet(rawKey, rawHashKey, rawHashValue);
                }
                return null;
            }
        });
        redisTemplate.expire(user2Key, 120, TimeUnit.SECONDS);
        System.out.println("user2, use time: "+ (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        String user3Key = "user3";
        final byte[] rawKey2 = getKeySerializeByte(user3Key);
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
//                connection.openPipeline();
                for (int i = 0; i < 1000; i++) {
                    final byte[] rawHashKey = getKeySerializeByte("status" + i);
                    final byte[] rawHashValue = getValueSerializeByte("value" + i);
                    connection.hSet(rawKey2, rawHashKey, rawHashValue);
                }
//                connection.closePipeline();
                return null;
            }
        });
        redisTemplate.expire(user3Key, 120, TimeUnit.SECONDS);
        System.out.println("user3, use time: "+ (System.currentTimeMillis() - start));
    }

    /**
     * 同时插入100个Key场景，调用结果：
     * multkey1, use time: 8752
     multkey2, use time: 3458
     multkey3, use time: 77
     */
    @Test
    public void testBatchMultKey(){
        long start = System.currentTimeMillis();
        String multkey1 = "multkey1";
        for (int i = 0; i < 1000; i++) {
            redisTemplate.opsForHash().put(multkey1 + i, "status", i+"");
            redisTemplate.expire(multkey1 + i, 120, TimeUnit.SECONDS);
        }
        System.out.println("multkey1, use time: "+ (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        String multkey2 = "multkey2";
        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
                for (int i = 0; i < 1000; i++) {
                    final byte[] rawKey = getKeySerializeByte(multkey2 + i);
                    final byte[] rawHashKey = getKeySerializeByte("status");
                    final byte[] rawHashValue = getValueSerializeByte(i+"");
                    connection.hSet(rawKey, rawHashKey, rawHashValue);
                    connection.expire(rawKey, 120);
//                    redisTemplate.expire(multkey2 + i, 120, TimeUnit.SECONDS);
                }
                return null;
            }
        });
        System.out.println("multkey2, use time: "+ (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        String multkey3 = "multkey3";

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection)
                    throws DataAccessException {
//                connection.openPipeline();
                for (int i = 0; i < 1000; i++) {
                    byte[] rawKey = getKeySerializeByte(multkey3 + i);
                    byte[] rawHashKey = getKeySerializeByte("status");
                    byte[] rawHashValue = getValueSerializeByte(i+"");
                    connection.hSet(rawKey, rawHashKey, rawHashValue);
                    connection.expire(rawKey, 120);
                }
//                connection.closePipeline();
                return null;
            }
        });
        System.out.println("multkey3, use time: "+ (System.currentTimeMillis() - start));
    }

    public byte[] getKeySerializeByte(String str){
        return redisTemplate.getKeySerializer().serialize(str);
    }

    public byte[] getValueSerializeByte(String str){
        return redisTemplate.getValueSerializer().serialize(str);
    }

    /**
     * dataCacheMap.size: 1000
     use time: 510
     */
    @Test
    public void testBatchCacheHash(){
        Map<String, String> dataCacheMap = new HashMap<>();
        String cacheKey = "xj:test:";
        for (int i=0; i<1000; i++){
            String devId = "devId" + i;
            String code = "wenDu";
            String key = devId + ":" + code;
            dataCacheMap.put(key, i+"");
        }
        long startTime = System.currentTimeMillis();
        System.err.println("dataCacheMap.size: " + dataCacheMap.size());
        redisUtils.batchCacheData(dataCacheMap, 120);
        System.err.println("use time: " + (System.currentTimeMillis() - startTime));
    }

    @Test
    public void testGetDevCache(){
        String ql = redisUtils.getFieldValueFromHash("zl:test:dev001", "QL");
        DataOutputBean dataOutputBean = JSONObject.parseObject(ql, DataOutputBean.class);
        System.err.println("dataOutputBean, devId: " + dataOutputBean.getDevId() + ", value: " + dataOutputBean.getParamValue() + " , code: " + dataOutputBean.getParamCode());
    }

    @Test
    public void testHashDevCache(){
        String ql = "zl:test:dev001";
        Map<String, String> devDataMap = redisTemplate.opsForHash().entries(ql);
        System.err.println("devDataMap: " + devDataMap);
        System.err.println("------------1-------------");
        System.err.println("QL: " + devDataMap.get("QL"));
        System.err.println("SW: " + devDataMap.get("SW"));
        System.err.println("YL: " + devDataMap.get("YL"));
        System.err.println("DY: " + devDataMap.get("DY"));
        System.err.println("------------2-------------");
        String devDataJson = JSONObject.toJSONString(devDataMap);
        Map<String, String> newDataMap = new HashMap<>();
        Map jsonToMap = JSONObject.parseObject(devDataJson, Map.class);
        System.err.println("jsonToMap : " + jsonToMap);
        System.err.println("------------3-------------");
    }


    @Test
    public void testRedisString(){
        String prefix = "AC:TEST:";
        DataOutputBean db1 = new DataOutputBean("dev001", "DY", "100", new Date());
        redisUtils.setJson(prefix + "TEMP", JSON.toJSONString(db1));
    }

    @Test
    public void testRedisString2(){
        String key = "AC:TEST:TMP1";
        redisUtils.setJson(key, "json");
        String value = String.valueOf(redisUtils.get(key));
        System.err.println("value: " + value);
    }

    @Test
    public void testCacheRealData(){
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("dev003:aa", "101");
        dataMap.put("dev003:bb", "102");
        dataMap.put("dev003:cc", "103");
        dataMap.put("dev004:00", "201");
        dataMap.put("dev004:11", "202");
        redisUtils.batchCacheData(dataMap, 1200);
    }

    @Test
    public void tesGetRealData(){
        String devId = "dev003";
        Set<String> codeSet = new HashSet<>();
        codeSet.add("aa");
        codeSet.add("bb");
        codeSet.add("cc");
//        Map<String, String> devCacheDataMap = redisUtils.getMapFromHash(GlobalCacheManage.PRE_DEV_REALDATA_CACHE + devId, codeSet);
//        Map<String, String> devCacheDataMap = redisUtils.getMapFromHash(GlobalCacheManage.PRE_DEV_REALDATA_CACHE + devId);

//        System.err.println("devCacheDataMap: " + devCacheDataMap);
        System.err.println("--------------------------------");
        List<QueryDataDto> queryDataDtos = new ArrayList<>();
        QueryDataDto dto = new QueryDataDto("dev003", null);
        queryDataDtos.add(dto);
        List<DevRealDataBean> reaList = devRealDataCache.queryDataByDevIds(queryDataDtos);
        String realJsonStr = JSONObject.toJSONString(reaList);
        System.err.println("reaList : " + realJsonStr);

        for (DevRealDataBean devRealDataBean : reaList) {
            System.err.println("reaList.map: " + devRealDataBean.getDevDataMap());
        }
        System.err.println("--------------------------------");
        List<DevRealDataBean> outPutList = JSONObject.parseArray(realJsonStr, DevRealDataBean.class);
        for (DevRealDataBean devRealDataBean : outPutList) {
            System.err.println("outPutList.aa: " + devRealDataBean.getDevDataMap().get("aa") + ", bb: " + devRealDataBean.getDevDataMap().get("bb") + ", cc: " +devRealDataBean.getDevDataMap().get("cc"));
        }
    }

}
