
package com.asiainfo.veris.crm.order.web.frame.bcf.base;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class CSBizHttpHandler extends BizHttpHandler
{

    public String getTradeEparchyCode() throws Exception
    {
        String tradeEparchyCode = getVisit().getStaffEparchyCode();

        if (StringUtils.isBlank(tradeEparchyCode) || tradeEparchyCode.length() != 4 || !StringUtils.isNumeric(tradeEparchyCode))
        {
            tradeEparchyCode = Route.getCrmDefaultDb();
        }

        return tradeEparchyCode;
    }
}
