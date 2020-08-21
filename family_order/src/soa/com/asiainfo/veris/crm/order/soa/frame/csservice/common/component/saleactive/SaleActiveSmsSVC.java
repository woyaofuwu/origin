
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SaleActiveSmsSVC extends CSBizService
{
    private static final long serialVersionUID = -9129400296266964757L;

    public IData sendVeriCodeSms(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String noticeContent = param.getString("NOTICE_CONTENT");
        int limitCount = Integer.parseInt(param.getString("LIMIT_COUNT"));
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        SaleActiveSmsBean saleActiveSmsBean = BeanManager.createBean(SaleActiveSmsBean.class);
        return saleActiveSmsBean.sendVeriCodeSms(serialNumber, userId, productId, packageId, noticeContent, limitCount, eparchyCode);
    }

    public void updVeriCodeOk(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String userId = param.getString("USER_ID");
        String productId = param.getString("PRODUCT_ID");
        String smsCode = param.getString("SMS_CODE");
        String eparchyCode = param.getString(Route.ROUTE_EPARCHY_CODE);

        SaleActiveSmsBean saleActiveSmsBean = BeanManager.createBean(SaleActiveSmsBean.class);
        saleActiveSmsBean.updVeriCodeOk(serialNumber, userId, productId, smsCode, eparchyCode);
    }

}
