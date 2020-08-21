
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.fetion;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class FetionIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {

        String operCode = input.getString("OPER_CODE");
        String passId = input.getString("PASS_ID");
        if (StringUtils.isNotBlank(passId))
        {
            input.put("RSRV_STR10", "PASS_ID:" + input.getString("PASS_ID", ""));

        }

        if (StringUtils.equals(operCode, PlatConstants.OPER_CANCEL_ORDER) || StringUtils.equals(operCode, "02"))
        {
            input.put("OPER_CODE", "04");
        }
    }

}
