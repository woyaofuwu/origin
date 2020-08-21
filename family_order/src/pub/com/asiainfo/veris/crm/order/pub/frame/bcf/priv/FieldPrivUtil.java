package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;

import java.util.LinkedList;
import java.util.Set;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public class FieldPrivUtil {

	/**
     * 根据员工工号过滤域权
     * 
     * @param staffId
     * @param discntList
     * @throws Exception
     */
    public static void filterFieldListByPriv(String staffId, IDataset fieldList) throws Exception
    {
        if (IDataUtil.isEmpty(fieldList))
        {
            return;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return;
        }

        LinkedList<String> fieldStrList = new LinkedList<String>();

        for (int i = 0, size = fieldList.size(); i < size; i++)
        {
            IData fieldData = fieldList.getData(i);

            String fieldCode = fieldData.getString("CODE", fieldData.getString("CODE"));

            fieldStrList.add(fieldCode);
        }

        Set<String> privDistSet = StaffPrivUtil.hasFieldPrivList(staffId, fieldStrList);

        if (privDistSet == null || privDistSet.size() == 0)
        {
        	fieldList.clear();
            return;
        }

        int row = fieldList.size();

        for (int i = row - 1; i >= 0; i--)
        {
            IData fieldData = fieldList.getData(i);

            String code = fieldData.getString("CODE");

            if (!privDistSet.contains(code))// 没有权限
            {
            	fieldList.remove(i);
            }
        }
    }
}
