
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;

/**
 * Copyright: Copyright 2014 5 20 Asiainfo-Linkage
 * 
 * @Description: 产品规则校验 --重构和优化
 * @author: xiaocl
 */
public class CheckProductData
{

    private static Logger logger = Logger.getLogger(CheckProductData.class);

    private IData ProductLimitOnCommpara;

    private String strAcctId;

    private String strAcctIdB;

    private String strCurDate;

    private String strCustId;

    private String strCustIdB;

    private String strEparchyCode;

    private String strExecTime;

    private String strFirstDayOfNextMonth;

    private String strInModeCode;

    private String strLastDayOfCurMonth;

    private String strProductId;

    private String strProvince;

    private String strRsrvStr10;

    private String strSpecTag;

    final String strSpeDiscntCode = "|";

    private String strTradeId = "";

    private String strTradeStaffId;

    private String strTradeTypeCode;

    private String strUserId;

    private String strUserIdB;

    /* 集团业务特殊情况加入,集团配置修改后,在修改时间点前加入的产品不判断产品必选包 */
    private String strSkipForcePackageForProduct = "0";

    public String getCurDate()
    {
        return strCurDate;
    }

    public String getEparchyCode()
    {
        return strEparchyCode;
    }

    public String getProductId()
    {
        return strProductId;
    }

    public String getSkipForcePackageForProduct()
    {
        return strSkipForcePackageForProduct;
    }

    public String getSpeDiscntCode()
    {
        return strSpeDiscntCode;
    }

    public String getStrFirstDayOfNextMonth()
    {
        return strFirstDayOfNextMonth;
    }

    public String getStrLastDayOfCurMonth()
    {
        return strLastDayOfCurMonth;
    }

    public String getTradeStaffId()
    {
        return strTradeStaffId;
    }

    public String getTradeTypeCode()
    {
        return strTradeTypeCode;
    }

    public String getUserId()
    {
        return strUserId;
    }

    public void setAcctIdB(String str)
    {
        strAcctIdB = str;
    }

    public void setCustIdB(String str)
    {
        strCustIdB = str;
    }

    public void setProvince(String str)
    {
        strProvince = str;
    }

    public void setRsrvStr10(String str)
    {
        strRsrvStr10 = str;
    }

    public void setSkipForcePackageForProduct(String str)
    {
        strSkipForcePackageForProduct = str;
    }

    public void setSpecTag(String str)
    {
        strSpecTag = str;
    }

    public void setStrAcctId(String str)
    {
        strAcctId = str;
    }

    @SuppressWarnings("unchecked")
    public void setStrCurDate(String str)
    {
        strCurDate = str;
    }

    public void setStrCustId(String str)
    {
        strCustId = str;
    }

    @SuppressWarnings("unchecked")
    public void setStrEparchyCode(String str)
    {
        strEparchyCode = str;
    }

    public void setStrExecTime(String str)
    {
        strExecTime = str;
    }

    @SuppressWarnings("unchecked")
    public void setStrFirstDayOfNextMonth(String str)
    {
        strFirstDayOfNextMonth = str;
    }

    public void setStrInModeCode(String str)
    {
        strInModeCode = str;
    }

    @SuppressWarnings("unchecked")
    public void setStrLastDayOfCurMonth(String str)
    {
        strLastDayOfCurMonth = str;
    }

    public void setStrProductId(String str)
    {
        strProductId = str;
    }

    public void setStrTradeId(String str)
    {
        strTradeId = str;
    }

    public void setstrTradeStaffId(String str)
    {
        strTradeStaffId = str;
    }

    public void setStrTradeTypeCode(String str)
    {
        strTradeTypeCode = str;
    }

    public void setStrUserId(String str)
    {
        strUserId = str;
    }

    public void setUserIdB(String str)
    {
        strUserIdB = str;
    }

}
