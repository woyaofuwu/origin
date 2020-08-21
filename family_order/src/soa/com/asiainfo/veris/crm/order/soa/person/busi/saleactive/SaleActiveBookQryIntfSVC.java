
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;

public class SaleActiveBookQryIntfSVC extends CSBizService
{
    /**
     * 根据手机号码查询用户营销活动预约信息
     * 
     * @param userId
     * @return
     * @throws Exception
     * @author huangsl
     * @date 2014-7-25
     */
    public static IDataset queryBookSaleActiveInfoBySn(IData params) throws Exception
    {
        IDataset results = HwTerminalCall.getTerminalInfoByTerminalId(params.getString("RES_NO"), params.getString("STAFF_ID"), params.getString("SERIAL_NUMBER"));
        return results;
    }
}
