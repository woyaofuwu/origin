
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.nfcp;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class NfcpIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("SP_CODE", input.getString("SP_CODE", input.getString("SP_ID")));

        // TODO Auto-generated method stub
        if (input.getString("OPR_NUMB") != null && !"".equals(input.getString("OPR_NUMB")))
        {
            input.put("INTF_TRADE_ID", input.getString("OPR_NUMB"));
        }
        // 01-手机钱包业务开通
        // 02-基于手机钱包业务的第三方上层应用开通（包月计费）需要二次确认
        // 03-下载优惠套餐包的开通
        // 04-手机钱包业务注销
        // 05-基于手机钱包业务的第三方上层应用注销
        // 06-下载优惠套餐包的退订（包月计费）
        if ("01".equals(input.getString("OPER_CODE")) || "02".equals(input.getString("OPER_CODE")) || "03".equals(input.getString("OPER_CODE")))
        {
            // 02的需要做二次确认，用Opr_source区分
            if ("02".equals(input.getString("OPER_CODE")))
            {
                input.put("OPR_SOURCE", "NF");
            }
            input.put("OPER_CODE", "06");
        }
        else if ("04".equals(input.getString("OPER_CODE")) || "05".equals(input.getString("OPER_CODE")) || "06".equals(input.getString("OPER_CODE")))
        {
            input.put("OPER_CODE", "07");
        }
        else
        {
            CSAppException.apperr(PlatException.CRM_PLAT_0904);
        }
    }

}
