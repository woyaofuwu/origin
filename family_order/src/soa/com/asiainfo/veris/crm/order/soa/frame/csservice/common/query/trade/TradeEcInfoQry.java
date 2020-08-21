
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeEcInfoQry
{

    /**
     * liaolc 2013-09-29 adcmas反向添加黑白名单 完工时修改TI_B_EC表的状态
     * 
     * @param orderId
     * @throws Exception
     */

    public static void updateAdcMasEc(String orderId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("ORDER_ID", orderId);
        Dao.executeUpdateByCodeCode("TI_B_EC", "UPD_STATE_FORM_COMPLETE", inparams);
    }
}
