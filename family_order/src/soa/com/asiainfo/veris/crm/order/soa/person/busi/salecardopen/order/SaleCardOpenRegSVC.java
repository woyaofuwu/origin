
package com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * 买断手工激活
 * 
 * @author sunxin
 */
public class SaleCardOpenRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    // @Override
    public String getOrderTypeCode() throws Exception
    {
        return "14";
    }

    // @Override
    public String getTradeTypeCode() throws Exception
    {
        return "14";
    }

    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (!"".equals(input.getString("PRODUCT_ID", "")) && (!"".equals(input.getString("PACKAGE_ID", ""))))
        {
            IData saleactiveData = new DataMap();
            saleactiveData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            saleactiveData.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
            saleactiveData.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        }
    }

}
