package com.ai.slp.route.api.util;

import com.ai.slp.common.api.cache.interfaces.ICacheSV;
import com.ai.slp.common.api.cache.param.SysParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CacheDic {

    private static ICacheSV iCacheSV;

    @Autowired
    private ICacheSV iCacheSVTmp;

    @PostConstruct
    public void initICacheSV() {
        CacheDic.iCacheSV = iCacheSVTmp;
    }


    public static String getAppKey(String tenantId, String typeCode, String paramCode) {
        SysParam sysParam = iCacheSV.getSysParam(tenantId, typeCode, paramCode, "APPKEY");
        return sysParam.getColumnDesc();
        //return "9db313541196135c37025f7a8021c4df";
    }

}
