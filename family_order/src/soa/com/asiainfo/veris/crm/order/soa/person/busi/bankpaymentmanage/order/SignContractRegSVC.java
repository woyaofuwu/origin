/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 下午04:10:27
 */
public class SignContractRegSVC extends OrderService
{

    private static final long serialVersionUID = 8438619636005725791L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1390";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1390";
    }
    
    @Override
    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setExecTime(btd.getMainTradeData().getExecTime());
    }


}
