
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustPersonInfoQry;

/**
 * 客户资料变更更新大客户资料信息
 * 
 * @author liutt
 */
public class ModifyBigCustAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        IDataset custPerTradeInfo = TradeCustPersonInfoQry.getTradeCustPersonByTradeId(mainTrade.getString("TRADE_ID"));
        IData custPerson = custPerTradeInfo.getData(0);

        IData params = new DataMap();
        params.put("TRADE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE"));
        params.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE"));
        params.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID"));
        params.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID"));
        params.put("X_CHECK_TAG", "201");
        params.put("CHECK_TAG", "201");
        params.put("X_GETMODE", "");
        params.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        params.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE"));
        params.put("CANCEL_TAG", mainTrade.getString("CANCEL_TAG"));
        params.put("USER_ID", mainTrade.getString("USER_ID"));
        params.put("CUST_ID", mainTrade.getString("CUST_ID"));
        params.put("ACCT_ID", mainTrade.getString("ACCT_ID"));
        params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        params.put("VIP_ID", "");
        params.put("GROUP_ID", "");
        params.put("RSRV_STR1", custPerson.getString("CUST_NAME"));
        params.put("RSRV_STR2", custPerson.getString("PSPT_TYPE_CODE"));
        params.put("RSRV_STR3", custPerson.getString("PSPT_ID"));
        if (StringUtils.isNotBlank(custPerson.getString("PSPT_END_DATE")))
        {
            params.put("RSRV_STR4", custPerson.getString("PSPT_END_DATE").substring(0, 10));
        }
        else
        {
            params.put("RSRV_STR4", "");
        }

        params.put("RSRV_STR5", custPerson.getString("PSPT_ADDR"));
        params.put("RSRV_STR6", custPerson.getString("PHONE"));
        params.put("RSRV_STR7", custPerson.getString("POST_ADDRESS"));
        if (StringUtils.isNotBlank(custPerson.getString("BIRTHDAY")))
        {
            params.put("RSRV_STR8", custPerson.getString("BIRTHDAY").substring(0, 10));
        }
        else
        {
            params.put("RSRV_STR8", "");
        }
        CSAppCall.call("CM.Comminterf", params);
    }

}
