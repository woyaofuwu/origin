
package com.asiainfo.veris.crm.order.soa.frame.bcf.base;

import com.ailk.biz.bean.BizBean;
import com.ailk.biz.service.BizRoute;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CSBizBean extends BizBean
{
    /**
     * 获取业务交易地址
     * 
     * @return
     * @throws Exception
     */
    public static String getTradeEparchyCode() throws Exception
    {
        String eparchyCode = BizRoute.getTradeEparchyCode();

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(BizException.CRM_BIZ_656);
        }

        return eparchyCode;
    }

    /**
     * 获取用户归属地址
     * 
     * @return
     * @throws Exception
     */
    public static String getUserEparchyCode() throws Exception
    {
        String eparchyCode = BizRoute.getUserEparchyCode();

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(BizException.CRM_BIZ_657);
        }

        return eparchyCode;
    }
}
