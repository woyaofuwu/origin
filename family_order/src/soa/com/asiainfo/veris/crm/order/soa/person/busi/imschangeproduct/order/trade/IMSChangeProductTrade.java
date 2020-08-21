
package com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.trade;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.person.busi.imschangeproduct.order.requestdata.IMSChangeProductRequestData;

public class IMSChangeProductTrade extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.trade.ChangeProductTrade implements ITrade
{
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception
    {
    	      
    	IMSChangeProductRequestData reqData = (IMSChangeProductRequestData) btd.getRD(); 
    	btd.getMainTradeData().setRsrvStr4(reqData.getIMSProductName());
    	btd.getMainTradeData().setRsrvStr5(reqData.getMobileSerialNumber());
    	btd.getMainTradeData().setRsrvStr6(reqData.getMobileProductId());

    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	super.createBusiTradeData(btd);
        appendTradeMainData(btd);

    }
}
