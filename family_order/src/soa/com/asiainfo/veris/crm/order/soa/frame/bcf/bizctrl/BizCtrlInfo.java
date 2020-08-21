
package com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class BizCtrlInfo
{
    private IData ctrlmap = new DataMap();

    /**
     * 获取产品控制信息中的预留字段1
     * 
     * @param attr
     * @return
     */
    public String getAttrStr1Value(String attr)
    {
        IData map = getCtrlData(attr);

        if (IDataUtil.isEmpty(map))
        {
            return "";
        }

        return map.getString("RSRV_STR1", "");
    }

    /**
     * 获取产品控制信息中的ATTR_VALUE
     * 
     * @param attr
     * @return
     */
    public String getAttrValue(String attr)
    {
        IData map = getCtrlData(attr);

        if (IDataUtil.isEmpty(map))
        {
            return "";
        }

        return map.getString("ATTR_VALUE", "");
    }

    /**
     * 获取产品控制信息中的反射类
     * 
     * @return
     */
    public String getCenCreateClass()
    {
        return getAttrValue("CenCreateClass");
    }

    /**
     * 获取产品控制信息中的反射类
     * 
     * @return
     */
    public String getCreateClass()
    {
        return getAttrValue("CreateClass");
    }

    public IData getCtrlData(String s)
    {
        return ctrlmap.getData(s);
    }

    public IData getCtrlInfo()
    {
        return ctrlmap;
    }

    /**
     * 获取产品控制信息中配置的付费列表
     * 
     * @return
     */
    public String getGrpPayList()
    {
        return getGrpPayList("ATTR_VALUE");
    }

    /**
     * 获取产品控制信息中配置的付费列表
     * 
     * @return
     */
    public String getGrpPayList(String key)
    {
        IData map = getCtrlData(key);
        return map.getString(key, "");
    }

    /**
     * 获取产品中的业务类型
     * 
     * @return
     */
    public String getTradeTypeCode()
    {
        return getTradeTypeCode("ATTR_VALUE");
    }

    /**
     * 获取产品控制信息中的业务类型
     * 
     * @param key
     * @return
     */
    public String getTradeTypeCode(String key)
    {
        IData map = getCtrlData("TradeTypeCode");

        if (IDataUtil.isEmpty(map))
        {
            return "";
        }

        return map.getString(key, "");
    }
}
