package com.zlst.common.redis;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.zlst.common.config.GlobalCacheManage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by 170099 on 2017/7/27.
 */
@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    public static final String OBJ_STRING_CACHE_FIELD = "objString";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void remove(final List<String> keys){
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (!keys.isEmpty())
            redisTemplate.delete(keys);
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Serializable get(final String key) {
        Serializable result = null;
        ValueOperations<Object, Serializable> operations = redisTemplate.opsForValue();
        result = operations.get(key);

        return result;
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public String getJson(final String key) {
        String result = null;
        ValueOperations<Object, String> operations = redisTemplate.opsForValue();
        result = operations.get(key);

        return result;
    }

    public byte[] getBytes(final String key){
        byte[] result;
        ValueOperations<Object, byte[]> operations = redisTemplate.opsForValue();
        result = operations.get(key);

        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Serializable value) {
        boolean result = false;
        try {
            ValueOperations<Object, Serializable> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("set error:", e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setJson(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations<Object, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            logger.error("setJson error:", e);
        }
        return result;
    }

    public boolean setByte(final String key, byte[] value){
        boolean result = false;
        try {
            ValueOperations<Object, byte[]> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        }catch (Exception e){
            logger.error("setBytes error", e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Set<? extends Serializable> value) {
        boolean result = false;
        try {
            ValueOperations<Object, Serializable> operations = redisTemplate.opsForValue();
            Serializable[] obj = new Serializable[value.size()];
            value.toArray(obj);
            operations.set(key, obj);
            result = true;
        } catch (Exception e) {
            logger.error("set string error:", e);
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Serializable value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Object, Serializable> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            logger.error("set serializable error:", e);
        }
        return result;
    }

    /**
     * 根据key获取自增后的值，Redis incr 可以实现原子性的递增，可应用于分布式序列号生成等场景
     *
     * @return
     */
    public long getincrementValue(String redisKey) {
        return redisTemplate.opsForValue().increment(redisKey, 1);
    }

    /**
     * 根据正则表达式获取对应的KEY值
     *
     * @param keyPattern
     * @return
     */
    public Set<String> keys(String keyPattern) {
        return redisTemplate.keys(keyPattern);
    }


    public List<String> multiGet(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public void multiSet(Map<String, String> keys) {
        redisTemplate.opsForValue().multiSet(keys);
    }


    public <T> T getObjFromHash(String key, Class<T> clazz) {
        if ((key == null) || (!redisTemplate.hasKey(key))) {
            return null;
        }
        HashOperations hashOperations = redisTemplate.opsForHash();
        Object obj = hashOperations.get(key, OBJ_STRING_CACHE_FIELD);
        if (obj == null) {
            return null;
        }
        return JSONObject.parseObject(obj.toString(), clazz);
    }

    public String getFieldValueFromHash(String key, String field) {
        if (null == key || null == field) {
            return null;
        }
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            Object obj = hashOperations.get(key, field);
            if (obj != null) {
                return obj.toString();
            }
        } catch (Exception e) {
            logger.error("get hash error key:" + key, e);
        }
        return null;
    }

    public String setFieldValueFromHash(String key, String field, String value) {
        if (null == key || null == field || null == value) {
            return null;
        }
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            Object hisObj = hashOperations.get(key, field);
            //这里暂时只put一次,没考虑put不进去的问题
            hashOperations.put(key, field, value);
            if (hisObj != null) {
                return hisObj.toString();
            }
        } catch (Exception e) {
            logger.error("set field hash error key:" + key, e);
        }
        return null;
    }

    public void setObjToHash(String key, Map<String, String> paras) {
        if (null == key || null == paras || paras.isEmpty()) {
            return;
        }
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, paras);
        } catch (Exception e) {
            logger.error("set obj hash error key:" + key, e);
        }
    }


    public void setObjToSet(String key, String... members) {
        if (null == key || null == members || members.length == 0) {
            return;
        }
        try {
            redisTemplate.delete(key);
            SetOperations setOperations = redisTemplate.opsForSet();
            setOperations.add(key, members);
        } catch (Exception e) {
            logger.error("set setObjToSet error key:" + key, e);
        }
    }

    public void setObjToSet(String key, List<String> members) {
        if (null == key || null == members || members.isEmpty()) {
            return;
        }
        String[] mps = new String[members.size()];
        members.toArray(mps);
        setObjToSet(key, mps);
    }

    public List<String> getListFromSet(String key) {
        if (null == key) {
            return new ArrayList<>();
        }
        try {
            SetOperations setOperations = redisTemplate.opsForSet();
            Set members = setOperations.members(key);
            List<String> rs = new ArrayList<>();
            rs.addAll(members);
            return rs;
        } catch (Exception e) {
            logger.error("getListFromSet error key:" + key, e);
        }
        return new ArrayList<>();
    }

    /**
     * 从redis里面读取list
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> getListFromList(String key, long start, long end) {
        if (null == key) {
            return new ArrayList<>();
        }
        try {
            ListOperations listOperations = redisTemplate.opsForList();
            Long size = listOperations.size(key);
            if (end > size - 1) {
                end = size - 1;
            }
            return listOperations.range(key, start, end);
        } catch (Exception e) {
            logger.error("getListFromList error key:" + key, e);
        }
        return new ArrayList<>();
    }

    public long removeFieldFromHash(String key, String field) {
        if (null == key || null == field) {
            return 0;
        }
        try {
            HashOperations hashOperations = redisTemplate.opsForHash();
            return hashOperations.delete(key, field);
        } catch (Exception e) {
            logger.error("remove has error key:" + key, e);
        }
        return 0;
    }

    /**
     * 获取指定Key对应Hash, 并转化为Map
     * @param key
     * @return
     */
    public Map<String, String> getMapFromHash(String key){
        if(StringUtils.isEmpty(key)){
            return null;
        }

        return redisTemplate.opsForHash().entries(key);
    }

    public Map<String, String> getMapFromHash(String key, Set<String> hashKeyMap){
        if(StringUtils.isEmpty(key)){
            return null;
        }
        Map<String, String> cacheMap = getMapFromHash(key);
        if(MapUtil.isNotEmpty(cacheMap)) {
            Map<String, String> resMap = new HashMap<>();
            cacheMap.forEach((devCode, value) -> {
                if (hashKeyMap.contains(devCode)) {
                    resMap.put(devCode, value);
                }
            });
            return resMap;
        }
        return null;
    }

    /**
     * 设置缓存有效期
     * @param key 缓存Key
     * @param effectiveTime 缓存有效时间
     * @param unit 时间单位
     */
    public void setCacheExpireTime(String key, long effectiveTime, TimeUnit unit){
        redisTemplate.expire(key, effectiveTime, unit);
    }

    /**
     * 批量保存数据
     * @param dataMap, key-devId:code, value-DataCacheBean
     * @param expire
     */
    public void batchCacheData(Map<String, String> dataMap, long expire) {
        if(MapUtil.isEmpty(dataMap)){
            logger.error("RedisUtils.batchCacheData, dataList is null");
            return;
        }

        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                dataMap.forEach((devCode, paramValue) -> {
                    String[] devArray = devCode.split(":");
                    String cacheKey = GlobalCacheManage.PRE_DEV_REALDATA_CACHE + devArray[0];
                    byte[] rawKey = getKeySerializeByte(cacheKey);
                    byte[] rawHashKey = getKeySerializeByte(devArray[1]);
                    byte[] rawHashValue = getKeySerializeByte(paramValue);
                    connection.hSet(rawKey, rawHashKey, rawHashValue);
                    connection.expire(rawKey, expire);
                });
                return null;
            }
        });
    }

    public byte[] getKeySerializeByte(String str){
        return redisTemplate.getKeySerializer().serialize(str);
    }

    public byte[] getValueSerializeByte(String str){
        return redisTemplate.getValueSerializer().serialize(str);
    }

}