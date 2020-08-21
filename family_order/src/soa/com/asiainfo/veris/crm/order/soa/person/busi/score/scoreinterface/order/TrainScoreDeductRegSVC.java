
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.order;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class TrainScoreDeductRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        String inModeCode = this.getVisit().getInModeCode();
        if (StringUtils.isNotBlank(inModeCode) && "WG".equals(inModeCode))
        {
            return "389";
        }
        else if (StringUtils.isNotBlank(inModeCode) && "40".equals(inModeCode))
        {
            return "387";
        }
        else
        {
            return "388";
        }
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        String inModeCode = this.getVisit().getInModeCode();
        if (StringUtils.isNotBlank(inModeCode) && "WG".equals(inModeCode))
        {
            return "389";
        }
        else if (StringUtils.isNotBlank(inModeCode) && "40".equals(inModeCode))
        {
            return "387";
        }
        else
        {
            return "388";
        }
    }

}
