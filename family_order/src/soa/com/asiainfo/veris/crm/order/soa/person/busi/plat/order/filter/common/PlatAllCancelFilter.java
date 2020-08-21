
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.common;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 处理电子渠道过来的全部业务退订
 * 
 * @author xiekl
 */
public class PlatAllCancelFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        String bizTypeCode = input.getString("BIZ_TYPE_CODE");
        if (StringUtils.isBlank(bizTypeCode))
        {
            input.put("BIZ_TYPE_CODE", "ALL_CANCEL");
        }
        input.put("OPER_CODE", PlatConstants.OPER_CANCEL_ALL);

    }

}
