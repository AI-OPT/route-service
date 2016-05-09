package com.ai.slp.route.common.util;

import com.ai.opt.sdk.components.mcs.MCSClientFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.slp.route.common.config.Constants;

import java.util.Map;

public class MCSUtil {

    public static String load(String key) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        return iCacheClient.get(key);
    }

    public static String hLoad(String key, String field) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        return iCacheClient.hget(key, field);
    }

    public static Map<String, String> hLoads(String key) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        return iCacheClient.hgetAll(key);
    }

    public static void expire(String key) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        iCacheClient.expire(key, 0);
    }

    public static void put(String key, String value, long expireTime) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        iCacheClient.set(key, value);
        iCacheClient.expireAt(key, expireTime);
    }

    public static void put(String key, String value) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        iCacheClient.set(key, value);
    }

    public static double atomIncrement(String key, float value) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        return iCacheClient.incrByFloat(key, value);
    }

    public static double atomDecrement(String key, float value) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        return iCacheClient.incrByFloat(key, -1 * value);
    }

    public static void putnx(String key, String value, long expireTime) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        iCacheClient.setnx(key, value);
        iCacheClient.expireAt(key, expireTime);
    }

    public static void hput(String key, Map<String, String> fields) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            iCacheClient.hset(key, entry.getKey(), entry.getValue());
        }
    }

    public static void hput(String key, String fields, String value) {
        ICacheClient iCacheClient = MCSClientFactory.getCacheClient(Constants.MCSConfig.REGISTER_NAMESPACE);
        iCacheClient.hset(key, fields, value);
    }


}
