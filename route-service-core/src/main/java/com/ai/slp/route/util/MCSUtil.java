package com.ai.slp.route.util;

import com.ai.opt.sdk.components.mcs.MCSClientFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.slp.route.constants.IPaasConfConstants;

import java.util.Map;

public class MCSUtil {

    public static String load(String key) {
        return getCacheClient().get(key);
    }

    public static String hLoad(String key, String field) {
        return getCacheClient().hget(key, field);
    }

    public static Map<String, String> hLoads(String key) {
        return getCacheClient().hgetAll(key);
    }

    public static Long expire(String key) {
        return getCacheClient().expire(key, 0);
    }

    public static void put(String key, String value, long expireTime) {
        ICacheClient iCacheClient = getCacheClient();
        iCacheClient.set(key, value);
        iCacheClient.expireAt(key, expireTime);
    }

    public static String put(String key, String value) {
        return getCacheClient().set(key, value);
    }

    public static double atomIncrement(String key, float value) {
        return getCacheClient().incrByFloat(key, value);
    }

    public static double atomDecrement(String key, float value) {
        return getCacheClient().incrByFloat(key, -1 * value);
    }

    public static void putnx(String key, String value, long expireTime) {
        ICacheClient iCacheClient = getCacheClient();
        iCacheClient.setnx(key, value);
        iCacheClient.expireAt(key, expireTime);
    }

    public static void hput(String key, Map<String, String> fields) {
        ICacheClient iCacheClient = getCacheClient();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            iCacheClient.hset(key, entry.getKey(), entry.getValue());
        }
    }

    public static void hput(String key, String fields, String value) {
        getCacheClient().hset(key, fields, value);
    }

    private static ICacheClient getCacheClient(){
        return MCSClientFactory.getCacheClient(IPaasConfConstants.MCSConfig.REGISTER_NAMESPACE);
    }

}
