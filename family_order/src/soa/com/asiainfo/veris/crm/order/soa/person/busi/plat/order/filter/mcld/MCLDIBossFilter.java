
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mcld;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class MCLDIBossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        if (!"".equals(input.getString("PASS_ID", "")))
        {
            input.put("RSRV_STR10", "PASS_ID:" + input.getString("PASS_ID", ""));
        }
        // 基础业务不允许退订-改成暂停 默认开通的需求
        if ("07".equals(input.getString("OPER_CODE")) && "YDCY".equals(input.getString("BIZ_CODE")) && "698027".equals(input.getString("SP_CODE")))
        {
            input.put("OPER_CODE", "04");
        }
    }

}
