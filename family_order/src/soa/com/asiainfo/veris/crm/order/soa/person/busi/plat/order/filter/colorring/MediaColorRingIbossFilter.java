
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.colorring;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class MediaColorRingIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        String operSource = input.getString("OPR_SOURCE");
        if (operSource == null || operSource.trim().equals(""))
        {
            input.put("OPR_SOURCE", "TD");
        }

        input.put("START_DATE", input.getString("ACCEPT_DATE"));// 受理时间

        String packageId = input.getString("PACK_NUMB");
        if (packageId != null && "".equals(packageId.trim()))
        {
            input.put("INFO_CODE", "Pack");
            input.put("INFO_VALUE", packageId);
        }
        String itemId = input.getString("ITEMID");// 赠送
        if (itemId != null && !"".equals(itemId.trim()) && input.getString("OPER_CODE").equals("11"))
        {
            input.put("INFO_CODE", input.get("ITEMID"));
            input.put("INFO_VALUE", input.get("ITEMCONT"));
        }

    }

}
