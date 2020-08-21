
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata;

import java.io.Serializable;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

/**
 * @author Administrator
 */
public abstract class BaseTradeData implements Serializable
{
    public abstract String getTableName();

    public abstract IData toData();

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }
}
