package com.zlst.common.redis;

import com.zlst.common.config.GlobalCacheManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * Desc:
 * redis 锁工具, 目前只做了单机模式的
 * @author wangshian/170137/C-2-08
 * @date 2018-03-07
 * @since CS-MES-T4-201801-B2
 * http://www.importnew.com/27477.html?utm_source=tuicool&utm_medium=referral
 * http://blog.csdn.net/lhn1234321/article/details/79000619
 * https://yq.aliyun.com/articles/339521?spm=a2c4e.11153940.blogcont328090.30.137a5d61QHN5vx
 */
@Component
public class RedisLockTool {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockTool.class);

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Long RELEASE_SUCCESS = 1L;

    private static final String LOCK_PRE = GlobalCacheManage.PRE_LOCK;

    private static String lockLua = "local key = KEYS[1] " +
                                    "local value = ARGV[1] " +
                                    "local outTime = tonumber(ARGV[2])  " +
                                    "local num = redis.call(\"setnx\",key,value) " +
                                    "if num == 1 then " +
                                    " redis.call(\"pexpire\",key,outTime)  "+
                                    "end  " +
                                    "return num ";

    private static String unLockLua =
                                    "local key =KEYS[1] " +
                                    "local value = ARGV[1] " +
                                    "if redis.call(\"get\",key) == value then " +
                                    " return redis.call(\"del\",key) " +
                                    "else " +
                                    " return 0 " +
                                    "end";

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @param expireTime 超期时间 单位为毫秒
     * @return 是否获取成功
     */
    public boolean lock(String lockKey, String requestId, long expireTime) {
        List<String> keys = new ArrayList<>();
        keys.add(LOCK_PRE + lockKey + "_" + requestId);
        RedisScript<Long> luaScript = new DefaultRedisScript<>(lockLua, Long.class);
        long result = (long) redisTemplate.execute(luaScript, keys, requestId, expireTime);
        return (result == RELEASE_SUCCESS);
    }

    public boolean tryLock(String lockKey, String requestId, long expireTime, long tryTime){
        List<String> keys = new ArrayList<>();
        keys.add(LOCK_PRE + lockKey + "_" + requestId);
        RedisScript<Long> luaScript = new DefaultRedisScript<>(lockLua, Long.class);
        long result = (long) redisTemplate.execute(luaScript, keys, requestId, expireTime);
        int tryCount = (int)tryTime / 100;
        while ((result != RELEASE_SUCCESS) && tryCount-- > 0) {
            try {
                result = (long) redisTemplate.execute(luaScript, keys, requestId, expireTime);
                Thread.sleep(100);
            } catch (Exception e) {
               logger.error("tryLock error:", e);
            }
        }
        return (result == RELEASE_SUCCESS);
    }


    /**
     * 释放分布式锁
     * @param lockKey 锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean unLock(String lockKey, String requestId) {
        List<String> keys = new ArrayList<>();
        keys.add(LOCK_PRE + lockKey + "_" + requestId);
        RedisScript<Long> luaScript = new DefaultRedisScript<>(unLockLua, Long.class);
        long result = (long) redisTemplate.execute(luaScript, keys, requestId);
        return (result == RELEASE_SUCCESS);
    }

}
