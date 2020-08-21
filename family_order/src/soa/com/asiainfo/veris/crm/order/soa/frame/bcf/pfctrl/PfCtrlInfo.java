
package com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class PfCtrlInfo
{
    private IData pfCtrlmap = new DataMap();

    /**
     * 获得取pf控制对象字段表达式
     * 
     * @param attr
     * @return
     */
    public String getFieldExpre(String attr)
    {
        IData map = getPfCtrlData(attr);
        if (IDataUtil.isEmpty(map))
            return "";
        return map.getString("FIELD_EXPRE", "");
    }

    /**
     * 获得pf控制对象字段值
     * 
     * @param attr
     * @return
     */
    public String getFieldValue(String attr)
    {
        IData map = getPfCtrlData(attr);
        if (IDataUtil.isEmpty(map))
            return "";
        return map.getString("FIELD_VALUE", "");
    }

    /**
     * 获得pf控制对象
     * 
     * @param s
     * @return
     */
    public IData getPfCtrlData(String s)
    {
        return pfCtrlmap.getData(s);
    }

    public IData getPfCtrlInfo()
    {
        return pfCtrlmap;
    }

    /**
     * 获取pf字段
     * 
     * @param attr
     * @return
     */
    public String getPfField(String attr)
    {
        IData map = getPfCtrlData(attr);
        if (IDataUtil.isEmpty(map))
            return "";
        return map.getString("PF_FIELD", "");
    }

}
