
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class OperFeeInfoQry
{
    /**
     * 获取营业费用
     * 
     * @author zhujm
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getFeeItem(String tradeTypeCode) throws Exception
    {
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        IDataset tmp = Dao.qryByCode("TD_B_OPERFEE", "SEL_OPERFEE", data, Route.CONN_CRM_CEN);
        return tmp;
    }

}
