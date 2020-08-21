
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderDeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BBossOrderDealSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /*
     * @description bboss瞬时报文处理
     * @author zhangcheng
     * @date 2013-08-15
     */
    public static IDataset dealBbossRspMessage(IData map) throws Exception
    {
        IDataset dealResultSet = BBossOrderDeal.dealBbossRspMessage(map);

        return dealResultSet;
    }
}
