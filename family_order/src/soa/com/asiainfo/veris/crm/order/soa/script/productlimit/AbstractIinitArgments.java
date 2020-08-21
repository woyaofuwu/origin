
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;

/**
 * Copyright: Copyright 2014 5 20 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化
 * @author: xiaocl
 */
public abstract class AbstractIinitArgments implements IinitArgments
{

    void initBase(IData databus, CheckProductData checkProductData) throws Exception
    {

        checkProductData.setStrFirstDayOfNextMonth(BreTimeUtil.getFirstDayOfNextMonth(databus));
        checkProductData.setStrLastDayOfCurMonth(BreTimeUtil.getFirstDayOfNextMonth(databus));
        checkProductData.setStrCurDate(BreTimeUtil.getCurDate(databus));
        IDataset listTrade = databus.getDataset("TF_B_TRADE");
        if (IDataUtil.isNotEmpty(listTrade))
        {
            databus.putAll(listTrade.getData(0));
        }

        if (databus.containsKey("TRADE_EPARCHY_CODE"))
        {
            checkProductData.setStrEparchyCode(databus.getString("TRADE_EPARCHY_CODE"));
        }
        if (databus.containsKey("TRADE_TYPE_CODE"))
        {
            checkProductData.setStrTradeTypeCode(databus.getString("TRADE_TYPE_CODE"));
        }
        if (databus.containsKey("IN_MODE_CODE"))
        {
            checkProductData.setStrTradeTypeCode(databus.getString("IN_MODE_CODE"));
        }
        if (databus.containsKey("TRADE_ID"))
        {
            checkProductData.setStrTradeTypeCode(databus.getString("TRADE_ID"));
        }
        if (databus.containsKey("PRODUCT_ID"))
        {
            checkProductData.setStrProductId(databus.getString("PRODUCT_ID"));
        }
        if (databus.containsKey("USER_ID"))
        {
            checkProductData.setStrUserId(databus.getString("USER_ID"));
        }
        if (databus.containsKey("EXEC_TIME"))
        {
            checkProductData.setStrExecTime(databus.getString("EXEC_TIME"));
        }
        if (databus.containsKey("TRADE_STAFF_ID"))
        {
            checkProductData.setstrTradeStaffId(databus.getString("TRADE_STAFF_ID"));
        }
        if (databus.containsKey("CUST_ID"))
        {
            checkProductData.setStrCustId(databus.getString("CUST_ID"));
        }
        if (databus.containsKey("ACCT_ID"))
        {
            checkProductData.setStrAcctId(databus.getString("ACCT_ID"));
        }
    }

}
