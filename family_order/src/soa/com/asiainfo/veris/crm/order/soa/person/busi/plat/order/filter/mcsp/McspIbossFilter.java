
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mcsp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 手机动漫Iboss入参转换
 * 
 * @author xiekl
 */
public class McspIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("OPR_SOURCE", input.getString("CHANNEL"));

    }

}
