package com.ai.slp.route.api.core.interfaces;

import com.ai.opt.base.exception.BusinessException;
import com.ai.opt.base.exception.SystemException;
import com.ai.slp.route.api.core.params.SaleProductInfo;

/**
 * 核心路由服务<br>
 * <p>
 * Date: 2016年4月18日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 *
 * @author zhangxin10
 */
public interface IRouteCoreService {

    /**
     * 根据售卖商品信息查询路由. <br>
     *
     * @param saleProductInfo 售卖商品信息
     * @return 路由ID
     * @throws BusinessException
     * @throws SystemException
     * @author zhangxin10
     * @ApiDocMethod
     * @ApiCode ROUTE_CORE_0001
     */
    String findRoute(SaleProductInfo saleProductInfo) throws SystemException;
}
