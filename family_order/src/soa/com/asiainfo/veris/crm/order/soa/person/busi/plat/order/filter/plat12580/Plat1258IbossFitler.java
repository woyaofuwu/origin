
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.plat12580;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 12580入参转换
 * 
 * @author xiekl
 */
public class Plat1258IbossFitler implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {

        String start = input.getString("ACCEPT_DATE");
        if (start != null && start.indexOf("-") < 0 && start.length() == 14)
        {
            start = start.substring(0, 4) + "-" + start.substring(4, 6) + "-" + start.substring(6, 8) + " " + start.substring(8, 10) + ":" + start.substring(10, 12) + ":" + start.substring(12);
        }
        input.put("START_DATE", start);

        if (StringUtils.isEmpty(input.getString("OPR_SOURCE")))
        {
            input.put("OPR_SOURCE", "06");
        }
        else
        {
            input.put("OPR_SOURCE", input.getString("OPR_SOURCE"));
        }
        if (StringUtils.isEmpty(input.getString("ORIGDOMAIN")))
        {
            input.put("ORG_DOMAIN", "1258");
        }

    }

}
