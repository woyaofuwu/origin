
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class ChangeProductIBossRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "110";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "110";
    }

    @Override
    public final void setTrans(IData input)
    {
        if ("6".equals(this.getVisit().getInModeCode()))
        {
            if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
            }

            /*
             * 转换外围选择的元素信息
             */

            IDataset orderProductInfo = input.getDataset("NEW_PRODUCT_INFO");
            if (null != orderProductInfo && orderProductInfo.size() > 0)
            {
                IDataset selectedElements = new DatasetList();

                for (int i = 0; i < orderProductInfo.size(); ++i)
                {
                    IData element = new DataMap();
                    element.put("ELEMENT_ID", orderProductInfo.getData(i).getString("ELEMENT_ID"));
                    element.put("ELEMENT_TYPE_CODE", orderProductInfo.getData(i).getString("ELEMENT_TYPE_CODE"));
                    element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);

                    IDataset eleAttr = orderProductInfo.getData(i).getDataset("ELEMENT_ATTR");
                    if (null != eleAttr && eleAttr.size() > 0)
                    {
                        IDataset attrSet = new DatasetList();
                        for (int j = 0; j < eleAttr.size(); ++j)
                        {
                            IData attr = new DataMap();
                            attr.put("ATTR_CODE", eleAttr.getData(j).getString("ATTR_CODE"));
                            attr.put("ATTR_VALUE", eleAttr.getData(j).getString("ATTR_VALUE"));
                            attrSet.add(attr);
                        }
                        element.put("ATTR_PARAM", attrSet);
                    }

                    selectedElements.add(element);
                }

                input.put("SELECTED_ELEMENTS", selectedElements);
            }
        }
    }
}
