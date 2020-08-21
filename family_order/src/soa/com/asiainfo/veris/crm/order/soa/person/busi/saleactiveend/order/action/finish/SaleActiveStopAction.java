
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class SaleActiveStopAction implements ITradeFinishAction
{
    public void executeAction(IData mainTrade) throws Exception
    {
        IDataset tradeSaleActive = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID"));
        if("1".equals(tradeSaleActive.getData(0).getString("RSRV_STR7")))
        {
	        IData param = new DataMap();
	        param.put("TRADE_ID",tradeSaleActive.getData(0).getString("TRADE_ID"));
	        param.put("USER_ID",tradeSaleActive.getData(0).getString("USER_ID"));
	        param.put("SERIAL_NUMBER",tradeSaleActive.getData(0).getString("SERIAL_NUMBER"));
	        param.put("PRODUCT_NAME",tradeSaleActive.getData(0).getString("PRODUCT_NAME"));
	        param.put("PACKAGE_NAME",tradeSaleActive.getData(0).getString("PACKAGE_NAME"));
	        param.put("CAMPN_TYPE",tradeSaleActive.getData(0).getString("CAMPN_TYPE"));
	        param.put("ADVANCE_PAY",tradeSaleActive.getData(0).getString("RSRV_STR4"));
	        param.put("SEND_PAY",tradeSaleActive.getData(0).getString("RSRV_STR5"));
	        param.put("RETURNFEE",tradeSaleActive.getData(0).getString("RSRV_STR6"));
	        param.put("ACCEPT_DATE",tradeSaleActive.getData(0).getString("UPDATE_TIME"));
	        param.put("TRADE_STAFF_ID",tradeSaleActive.getData(0).getString("UPDATE_STAFF_ID"));
	        param.put("TRADE_CITY_CODE",tradeSaleActive.getData(0).getString("RSRV_STR8"));
	        param.put("START_DATE",tradeSaleActive.getData(0).getString("START_DATE"));
	        param.put("END_DATE",tradeSaleActive.getData(0).getString("END_DATE"));
	        param.put("RSRV_STR1",tradeSaleActive.getData(0).getString("RSRV_STR7"));
	        param.put("RSRV_STR2",tradeSaleActive.getData(0).getString("RSRV_STR2"));
	        param.put("RSRV_STR3",tradeSaleActive.getData(0).getString("RSRV_STR3"));
	        param.put("REMARK",tradeSaleActive.getData(0).getString("RSRV_STR25"));
	        param.put("INST_ID",tradeSaleActive.getData(0).getString("INST_ID"));
	        Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "INSERT_SALE_STOP_ALL", param);
	        
        }

    }

}
