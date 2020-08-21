
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.service.BizService;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CSBizServiceAee extends BizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 服务初始化,重载
     */
    @Override
    public final void initialize(IData input) throws Exception
    {
        // 设置 服务路由ID
        String routeId = input.getString(Route.ROUTE_EPARCHY_CODE);

        // 路由ID
        if (StringUtils.isBlank(routeId))
        {
            CSAppException.apperr(BizException.CRM_BIZ_650);
        }

        setRouteId(routeId);

        // 设置 用户归属地州
        String userEparchyCode = input.getString(Route.USER_EPARCHY_CODE, "");

        setUserEparchyCode(userEparchyCode);

        // 设置 交易地州
        String tradeEparchyCode = getVisit().getStaffEparchyCode();

        setTradeEparchyCode(tradeEparchyCode);

        // 设置拦截器
        setIntercept();
    }

    /**
     * 设置拦截器
     * 
     * @throws Exception
     */
    public void setIntercept() throws Exception
    {
        setMethodIntercept(CSBizInterceptAee.class.getName());
    }
}
