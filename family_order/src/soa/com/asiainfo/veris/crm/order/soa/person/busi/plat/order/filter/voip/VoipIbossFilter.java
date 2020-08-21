
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.voip;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class VoipIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        if (StringUtils.isNotBlank(input.getString("OPR_NUMB")))
        {
            input.put("INTF_TRADE_ID", input.getString("OPR_NUMB"));
        }
        if ("BIP2B402_T2040072_1_0".equals(input.getString("KIND_ID")))
        {
            input.put("OPER_CODE", PlatConstants.OPER_ORDER);
            input.put("RSRV_STR8", input.getString("OPER_CODE")); 
        }
        if ("BIP2B406_T2101056_1_0".equals(input.getString("KIND_ID")))
        {
            input.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
        }
    }

}
