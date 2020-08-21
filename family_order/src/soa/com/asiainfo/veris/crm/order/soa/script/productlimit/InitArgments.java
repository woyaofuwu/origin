
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;

/**
 * Copyright: Copyright 2014 5 30 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化 海南特有
 * @author: xiaocl
 */
public class InitArgments implements IinitArgments
{
    InitArgments(IData databus, CheckProductData checkProductData) throws Exception
    {
        initArgments(databus, checkProductData);
    }

    public void initArgments(IData databus, CheckProductData checkProductData) throws Exception
    {

        checkProductData.setStrFirstDayOfNextMonth(BreTimeUtil.getFirstDayOfNextMonth(databus));
        checkProductData.setStrLastDayOfCurMonth(BreTimeUtil.getLastDayOfCurMonth(databus));

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
            checkProductData.setStrInModeCode(databus.getString("IN_MODE_CODE"));
        }
        if (databus.containsKey("TRADE_ID"))
        {
            checkProductData.setStrTradeId(databus.getString("TRADE_ID"));
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

        if (checkProductData.getEparchyCode().equals("INTF"))
        {
            checkProductData.setStrEparchyCode(BizRoute.getRouteId());
        }

        if (databus.containsKey("SPEC_TAG"))
        {
            checkProductData.setSpecTag(databus.getString("SPEC_TAG"));
        }
        else
        {
            checkProductData.setstrTradeStaffId(databus.getString("NO"));
        }

        if (databus.containsKey("PROVINCE_CODE"))
        {
            checkProductData.setProvince(databus.getString("PROVINCE_CODE"));
        }
        else
        {
            IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(checkProductData.getEparchyCode(), "PUB_CUR_PROVINCE", "PUB", "0");

            if (listTag.size() == 0)
            {
                checkProductData.setProvince("HAIN");
            }
            else if (listTag.size() == 1)
            {
                checkProductData.setProvince((String) listTag.get(0, "TAG_INFO"));
            }
        }

        // 一卡双号副号码三户标识 Begin
        if (databus.containsKey("RSRV_STR7"))
        {
            checkProductData.setUserIdB((databus.getString("RSRV_STR7")));
        }
        if (databus.containsKey("RSRV_STR9"))
        {
            checkProductData.setCustIdB(databus.getString("RSRV_STR9"));
        }
        if (databus.containsKey("RSRV_STR10"))
        {
            checkProductData.setAcctIdB(databus.getString("RSRV_STR10"));
        }
        // 一卡双号副号码三户标识 End

        if (databus.containsKey("RSRV_STR10"))
        {
            checkProductData.setRsrvStr10(databus.getString("RSRV_STR10", ""));
        }

        if (databus.containsKey("SKIP_FORCE_PACKAGE_FOR_PRODUCT"))
        {
            checkProductData.setSkipForcePackageForProduct(databus.getString("SKIP_FORCE_PACKAGE_FOR_PRODUCT", "0"));
        }
    }

}
