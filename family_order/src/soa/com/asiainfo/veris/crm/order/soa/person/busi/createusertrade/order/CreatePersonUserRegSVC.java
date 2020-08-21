
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreatePersonUserRegSVC extends OrderService
{

    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("ORDER_TYPE_CODE", "10");
    }

    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "10");
    }

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

    /**
     * 开户登记
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeReg(IData input) throws Exception
    {
        return super.tradeReg(input);
    }
}
