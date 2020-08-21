
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

import java.util.List;

public class CreateNpUserTradeRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "40";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "40";
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd)
    {
        orderData.setExecTime(SysDateMgr.END_DATE_FOREVER);
    }

    /**
     * 携号转网背景下吉祥号码业务规则优化需求（上） by mengqx
     * @param input
     * @param btd
     * @throws Exception
     */
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (input.getString("BIND_SALE_TAG", "").equals("1"))
        {
            IData saleactiveData = new DataMap();
            saleactiveData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            saleactiveData.put("PRODUCT_ID", input.getString("SALE_PRODUCT_ID"));
            saleactiveData.put("PACKAGE_ID", input.getString("SALE_PACKAGE_ID"));
            String acctDay = "";
            String firstDate = "";
            String nextAcctDay = "";
            String nextFirstDate = "";
            List<UserAcctDayTradeData> userAcctDayTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_USER_ACCTDAY);
            for (UserAcctDayTradeData userAcctDayTradeData : userAcctDayTradeDatas)
            {
                acctDay = userAcctDayTradeData.getAcctDay();
                firstDate = userAcctDayTradeData.getFirstDate();
                nextAcctDay = userAcctDayTradeData.getAcctDay();
                nextFirstDate = userAcctDayTradeData.getFirstDate();
            }

            btd.getRD().getUca().setAcctDay(acctDay);
            btd.getRD().getUca().setFirstDate(firstDate);
            btd.getRD().getUca().setNextAcctDay(nextAcctDay);
            btd.getRD().getUca().setNextFirstDate(nextFirstDate);
            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData);
        }
    }
}
