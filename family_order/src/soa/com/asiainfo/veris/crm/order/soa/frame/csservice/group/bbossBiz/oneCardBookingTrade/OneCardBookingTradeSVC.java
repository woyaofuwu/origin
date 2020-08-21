
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardBookingTrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class OneCardBookingTradeSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 一卡通预约订购
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public static IDataset ecBizInfoReq(IData inputData) throws Exception
    {
        IDataset dataset = OneCardBookingTrade.ecBizInfoReq(inputData);

        return dataset;
    }
}
