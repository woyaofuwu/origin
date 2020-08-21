
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.farm;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

/**
 * 农信通同步接口
 * 
 * @author xiekl
 */
public class RinpSyncfilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        input.put("IN_MODE_CODE", "6");// 订购关系同步不传in_mode_code
        // 03全退订
        if ("03".equals(input.getString("OPER_CODE")))
        {
            input.put("OPER_CODE", PlatConstants.OPER_CANCEL_ALL);
        }
        else if ("01_02".indexOf(input.getString("OPER_CODE")) >= 0)
        {
            if (StringUtils.isBlank(input.getString("BIZ_CODE", "")) || StringUtils.isBlank(input.getString("CONTENT_TYPE", "")) || StringUtils.isBlank(input.getString("CONTENT_CODE", "")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_2998_3, "业务代码，内容编排类型，内容编码必填！");
            }
            else
            {
                input.put("BIZ_CODE", input.getString("BIZ_CODE") + "-" + input.getString("CONTENT_TYPE") + "-" + input.getString("CONTENT_CODE"));
            }
        }
        else
        {
            CSAppException.apperr(PlatException.CRM_PLAT_2998_3, "操作代码错误");
        }
        if ("".equals(input.getString("KIND_ID", "")))
        {
            input.put("KIND_ID", "BIP2B151_T2001049_1_0");
        }
        if (StringUtils.isBlank(input.getString("BIPCODE", "")))
        {
            input.put("BIPCODE", "BIP2B151");
        }
        if (StringUtils.isBlank(input.getString("ACTIVITYCODE", "")))
        {
            input.put("ACTIVITYCODE", "T2001049");
        }
        if (StringUtils.isBlank(input.getString("ORIGDOMAIN", "")))
        {
            input.put("ORIGDOMAIN", "RINP");
        }
        if (StringUtils.isBlank(input.getString("HOMEDOMAIN", "")))
        {
            input.put("HOMEDOMAIN", "BOSS");
        }
        if (StringUtils.isBlank(input.getString("BIZ_TYPE_CODE", "")))
        {
            input.put("BIZ_TYPE_CODE", "27");
        }

        return;
    }

}
