
package com.asiainfo.veris.crm.order.pub.frame.bcf.priv;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.rpc.org.jboss.netty.util.internal.StringUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public final class ElementPrivUtil
{

    /**
     * 根据员工工号过滤元素列表中有权限的元素
     * 
     * @param staffId
     * @param elementList
     * @throws Exception
     */
    public static void filterElementListByPriv(String staffId, IDataset elementList) throws Exception
    {
        if (IDataUtil.isEmpty(elementList))
        {
            return;
        }

        if ("SUPERUSR".equals(staffId))
        {
            return;
        }

        IDataset disList = new DatasetList();
        IDataset svcList = new DatasetList();
        IDataset otherList = new DatasetList();

        for (int i = 0; i < elementList.size(); i++)
        {
            IData elementData = elementList.getData(i);

            String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE");
            if(StringUtils.isEmpty(elementTypeCode))
            {
                elementTypeCode = elementData.getString("OFFER_TYPE");
                if(StringUtils.isEmpty(elementData.getString("ELEMENT_ID")))
                {
                    elementData.put("ELEMENT_ID", elementData.getString("OFFER_CODE"));
                }
            }

            if ("S".equals(elementTypeCode))
            {
                svcList.add(elementData);
            }
            else if ("D".equals(elementTypeCode))
            {
                disList.add(elementData);
            }
            else
            {
                otherList.add(elementData);
            }
        }

        DiscntPrivUtil.filterDiscntListByPriv(staffId, disList);

        SvcPrivUtil.filterSvcListByPriv(staffId, svcList);

        elementList.clear();
        elementList.addAll(disList);
        elementList.addAll(svcList);
        elementList.addAll(otherList);
    }

}
