
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivetrans;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveTransCheckSVC extends CSBizService
{
    private static final long serialVersionUID = -7377339982268992555L;

    public void checkSourceUser(IData params) throws Exception
    {
        String sourceSn = params.getString("SERIAL_NUMBER");
        String activeSate = params.getString("ACTIVE_STATE");
        String endDate = params.getString("END_DATE");
        String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
        SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
        saleActiveTransBean.checkSourceUser(sourceSn, activeSate, endDate, eparchyCode);
    }

    public void checkTargetUser(IData params) throws Exception
    {
        String sourceSn = params.getString("SERIAL_NUMBER");
        String targetSn = params.getString("TARGET_SERIAL_NUMBER");
        String productId = params.getString("PRODUCT_ID");
        String eparchyCode = params.getString(Route.ROUTE_EPARCHY_CODE);
        SaleActiveTransBean saleActiveTransBean = BeanManager.createBean(SaleActiveTransBean.class);
        saleActiveTransBean.checkTargetUser(sourceSn, targetSn, productId, eparchyCode, params);
    }
}
