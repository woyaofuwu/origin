
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mail139;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class MailIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("RSRV_NUM1", input.getString("REASON_CODE", ""));// REMARK-->RSRV_NUM1
        if (PlatConstants.OPER_USER_DATA_MODIFY.equals(input.getString("OPER_CODE")))
        {
            // 如果是套餐变更操作,则转成订购
            input.put("RSRV_STR8", PlatConstants.OPER_USER_DATA_MODIFY);
            input.put("OPER_CODE", PlatConstants.OPER_ORDER);
        }
        if (!"".equals(input.getString("PASS_ID", "")))
        {
            input.put("RSRV_STR10", "PASS_ID:" + input.getString("PASS_ID", ""));
        }
        // 基础业务不允许退订-改成暂停//SP_CODE=926275 BIZ_CODE=+MAILMF 默认开通需求
        if ("07".equals(input.getString("OPER_CODE")) && "+MAILMF".equals(input.getString("BIZ_CODE")) && "920931".equals(input.getString("SP_CODE")))
        {
            input.put("OPER_CODE", "04");
        }
    }

}
