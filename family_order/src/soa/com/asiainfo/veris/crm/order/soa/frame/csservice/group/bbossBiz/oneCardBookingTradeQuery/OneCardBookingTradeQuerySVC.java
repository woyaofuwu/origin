
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.oneCardBookingTradeQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class OneCardBookingTradeQuerySVC extends CSBizService
{

    /*
     * @description bboss瞬时报文处理 一卡通业务查询
     * @author zhangcheng
     * @date 2013-08-15
     */
    public static IDataset ecBizInfoReqSelect(IData map) throws Exception
    {
        // 1- 定义返回对象
        IDataset dealResultSet = new DatasetList();

        dealResultSet = OneCardBookingTradeQuery.ecBizInfoReqSelect(map);

        return dealResultSet;
    }
}
