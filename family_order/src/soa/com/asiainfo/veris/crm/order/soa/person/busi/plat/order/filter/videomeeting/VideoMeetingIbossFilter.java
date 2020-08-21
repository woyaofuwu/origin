
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.videomeeting;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class VideoMeetingIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        String operSource = input.getString("OPR_SOURCE");
        if (operSource == null || operSource.trim().equals(""))
        {
            input.put("OPR_SOURCE", "TD");
        }
    }

}
