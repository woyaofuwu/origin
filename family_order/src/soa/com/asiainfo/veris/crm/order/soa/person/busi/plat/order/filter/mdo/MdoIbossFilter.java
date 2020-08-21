
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mdo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * @author xiekl
 */
public class MdoIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("OPR_SOURCE", input.getString("CHANNEL"));

        if (!"04".equals(input.getString("OPR_SOURCE")))
        {// 平台侧非短信渠道 不走pf
            input.put("IS_NEED_PF", "0");
        }
    }

}
