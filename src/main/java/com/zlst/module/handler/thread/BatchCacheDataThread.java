package com.zlst.module.handler.thread;

import cn.hutool.core.map.MapUtil;
import com.netflix.discovery.converters.Auto;
import com.zlst.common.config.GlobalCacheManage;
import com.zlst.common.redis.RedisUtils;
import com.zlst.module.handler.cache.DevRealDataCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 批量缓存数据线程， 已修改为通过Spring Task实现
 * @author xiong.jie/170123
 */
@Deprecated
public class BatchCacheDataThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(BatchCacheDataThread.class);

    private RedisUtils redisUtils;

    private static ConcurrentHashMap<String, String> realDataCache;

    /**
     * 轮训周期为2秒
     */
    private static final int period = 2 * 1000;

    public BatchCacheDataThread() {
        super();
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (MapUtil.isNotEmpty(realDataCache)) {
                    redisUtils.batchCacheData(realDataCache, GlobalCacheManage.REALDATA_EFFTIME);
                    LOG.info("BatchCacheDataThread.run, 执行批量数据缓存成功， realDataCache.size:{}", realDataCache.size());
                }
            } catch (Exception err) {
                LOG.error("BatchCacheDataThread.run, 出现异常:{}", err);
            }
            sleepThread();
        }
    }

    public void sleepThread() {
        try {
            Thread.sleep(period);
        } catch (InterruptedException e) {
            LOG.error("BatchCacheDataThread.sleepThread, 关闭线程失败, e:{}", e);
            Thread.currentThread().interrupt();
        }
    }
}