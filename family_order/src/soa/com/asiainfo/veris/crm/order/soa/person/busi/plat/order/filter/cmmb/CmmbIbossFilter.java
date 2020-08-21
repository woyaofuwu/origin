
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.cmmb;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

/**
 * 手机电视 一级boss转换
 * 
 * @author bobo
 */
public class CmmbIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        if ("".equals(input.getString("OPER_CODE", "")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "操作码OPER_CODE不能为空");
        }
        if ("".equals(input.getString("SERIAL_NUMBER", "")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "手机号SERIAL_NUMBER不能为空");
        }
        if ("".equals(input.getString("INTF_TRADE_ID", "")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "操作流水INTF_TRADE_ID不能为空");
        }
        if ("".equals(input.getString("BIZ_TYPE_CODE", "")))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "业务类型BIZ_TYPE_CODE不能为空");
        }

        // TODO Auto-generated method stub
        if ("13".equals(input.getString("OPER_CODE")))
        {
            input.put("OPER_CODE", PlatConstants.OPER_ORDER);
        }

        // iboss过来的手机电视，需要调用iboss的确认接口
        if ("6".equals(CSBizBean.getVisit().getInModeCode()) && "53".equals(input.getString("OPR_SOURCE")))
        {
            IData result = IBossCall.confirmCMMB(input.getString("OPER_CODE"), input.getString("SERIAL_NUMBER"), input.getString("INTF_TRADE_ID"), "53", input.getString("SP_CODE", "REG_SP"), input.getString("BIZ_CODE", "REG_SP"), "00");

            if (!"0".equals(result.getString("X_RSPTYPE", "")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0999_1, result.getString("X_RSPCODE") + "一级boss确认接口出错");
            }

        }

    }
}
