
package com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.UPFDetailInfoQry;

public final class PfCtrlBean
{
    /**
     * 获得IS_PF控制信息
     * 
     * @return
     * @throws Exception
     */
    public static PfCtrlInfo getPfCtrlInfo() throws Exception
    {
        // 构造
        PfCtrlInfo pfCtrlInfo = new PfCtrlInfo();

        IDataset ids = UPFDetailInfoQry.qryPfDetail();

        // 没有配置
        if (IDataUtil.isEmpty(ids))
        {
            return pfCtrlInfo;
        }

        Iterator<Object> iterator = ids.iterator();

        if (!iterator.hasNext())
        {
            return pfCtrlInfo;
        }

        IData data = null;
        String key = "";
        IData crt = pfCtrlInfo.getPfCtrlInfo();

        while (iterator.hasNext())
        {
            // key value
            data = (IData) iterator.next();
            key = data.getString("TABLE_NAME", "");

            crt.put(key, data);
        }

        return pfCtrlInfo;
    }
}
