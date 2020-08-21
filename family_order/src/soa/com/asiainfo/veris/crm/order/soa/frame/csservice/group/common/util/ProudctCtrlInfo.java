
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public final class ProudctCtrlInfo
{
    private IData map = new DataMap();

    public String getAttrValue(String attr)
    {
        return getCtrlData(attr).getString("ATTR_VALUE", "");
    }

    public String getCenCreateClass()
    {
        return getAttrValue("CenCreateClass");
    }

    public String getCreateClass()
    {
        return getAttrValue("CreateClass");
    }

    public IData getCtrlData(String s)
    {
        return map.getData(s);
    }

    public IData getCtrlInfo()
    {
        return map;
    }

    public String getGrpPayList()
    {
        return getGrpPayList("ATTR_VALUE");
    }

    public String getGrpPayList(String key)
    {
        return getCtrlData("GrpPayList").getString(key, "");
    }

    public String getTradeTypeCode()
    {
        return getTradeTypeCode("ATTR_VALUE");
    }

    public String getTradeTypeCode(String key)
    {
        return getCtrlData("TradeTypeCode").getString(key, "");
    }
}
