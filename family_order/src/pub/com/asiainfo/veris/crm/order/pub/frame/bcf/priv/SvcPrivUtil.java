
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class SvcPrivUtil
{
    /**
     * 根据员工工号过滤服务列表并获取有权限的服务
     * 
     * @param staffId
     * @param svcList
     * @throws Exception
     */
    public static void filterSvcListByPriv(String staffId, IDataset svcList) throws Exception
    {
        if ("SUPERUSR".equals(staffId) || IDataUtil.isEmpty(svcList))
        {
            return;
        }

        LinkedList<String> svcStrList = new LinkedList<String>();

        for (int i = 0; i < svcList.size(); i++)
        {
            IData svcData = svcList.getData(i);
            String svcCode = svcData.getString("SERVICE_ID", svcData.getString("ELEMENT_ID"));
            svcStrList.add(svcCode);
        }

        Set<String> privStrSet = StaffPrivUtil.hasSvcPrivList(staffId, svcStrList);

        if (privStrSet == null || privStrSet.size() == 0)
        {
            svcList.clear();
            return;
        }

        int row = svcList.size();

        for (int i = row - 1; i >= 0; i--)
        {
            IData svcData = svcList.getData(i);
            String svcCode = svcData.getString("SERVICE_ID", svcData.getString("ELEMENT_ID"));
            if (!privStrSet.contains(svcCode))// 没有权限
            {
                svcList.remove(i);
            }
        }

    }

}
