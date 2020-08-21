
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class CreatePersonUserIntfSVC extends OrderService
{

    /*
     * public IData tradeReg(IData input) throws Exception{ String url = GlobalCfg.getProperty("service.router.addr",
     * null); IDataInput inputData = DataHelper.createDataInput(getVisit(), input, null);
     * inputData.getHead().put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
     * inputData.getHead().put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
     * inputData.getHead().put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE"));
     * inputData.getHead().put("TRADE_EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE"));
     * inputData.getHead().put(Route.ROUTE_EPARCHY_CODE, input.getString(Route.ROUTE_EPARCHY_CODE));
     * inputData.getHead().put("IN_MODE_CODE", getVisit().getInModeCode()); return CSAppCall.call(url,
     * "SS.CreatePersonUserRegSVC.tradeReg", inputData, true).getData(0); }
     */

    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", "10");
    }

    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "10");
    }
    
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        if (StringUtils.isNotEmpty(input.getString("OAO_PRODUCT_ID"))
        		&&StringUtils.isNotEmpty(input.getString("OAO_PRODUCT_ID")))
        {
            IData saleactiveData = new DataMap();
            saleactiveData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            saleactiveData.put("PRODUCT_ID", input.getString("OAO_PRODUCT_ID"));
            saleactiveData.put("PACKAGE_ID", input.getString("OAO_PACKAGE_ID"));
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
