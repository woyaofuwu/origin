
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class DiscntPrivUtil
{
    /**
     * 根据员工工号过滤资费列表中有权限的资费
     * 
     * @param staffId
     * @param discntList
     * @throws Exception
     */
    public static void filterDiscntListByPriv(String staffId, IDataset discntList) throws Exception
    {
        if (IDataUtil.isEmpty(discntList))
        {
            return;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return;
        }

        LinkedList<String> disStrList = new LinkedList<String>();

        for (int i = 0, size = discntList.size(); i < size; i++)
        {
            IData discntData = discntList.getData(i);

            String discntCode = discntData.getString("DISCNT_CODE", discntData.getString("ELEMENT_ID"));

            disStrList.add(discntCode);
        }

        Set<String> privDistSet = StaffPrivUtil.hasDistPrivList(staffId, disStrList);

        if (privDistSet == null || privDistSet.size() == 0)
        {
            discntList.clear();
            return;
        }

        int row = discntList.size();

        for (int i = row - 1; i >= 0; i--)
        {
            IData discntData = discntList.getData(i);

            String discntCode = discntData.getString("DISCNT_CODE", discntData.getString("ELEMENT_ID"));

            if (!privDistSet.contains(discntCode))// 没有权限
            {
                discntList.remove(i);
            }
        }
    }
}
