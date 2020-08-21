
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.blby;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.AttrTrans;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class BLBYEChannelFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset attrs = new DatasetList();
        if ("01".equals(input.getString("OPER_CODE")))
        {
            input.put("INFO_CODE", "BLBY_SERV_TYPE_PSON");
            input.put("INFO_VALUE", input.getString("INFO_VALUE"));

            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "3012");
            attrParam.put("ATTR_VALUE", "1");
            attrs.add(attrParam);
        }

        AttrTrans.trans(input);

        if (IDataUtil.isNotEmpty(attrs))
        {
            IDataset attrParams = input.getDataset("ATTR_PARAM");
            if (IDataUtil.isEmpty(attrParams))
            {
                input.put("ATTR_PARAM", attrs);
            }
            else
            {
                attrParams.addAll(attrs);
            }
        }

    }

}
