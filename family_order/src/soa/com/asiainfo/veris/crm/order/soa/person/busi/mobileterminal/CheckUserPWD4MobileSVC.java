
package com.asiainfo.veris.crm.order.soa.person.busi.mobileterminal;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CheckUserPWD4MobileSVC extends CSBizService
{
    /**
	 * 
	 */

    public IData checkUserPWD(IData data) throws Exception
    {
        // 校验入参
        this.validParams(data);
        /*
         * //转换入参 this.setTrans(data);
         */
        // 校验并返回信息
        CheckUserPWD4MobileBean checkUserPWD4MobileBean = new CheckUserPWD4MobileBean();

        return checkUserPWD4MobileBean.checkUserPWD(data);

    }

    public final void setTrans(IData input)
    {
        {
            if (!"".equals(input.getString("IDVALUE", "")))
            {
                input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
            }
        }
    }

    private void validParams(IData data) throws Exception
    {
        if ("".equals(data.getString("IDTYPE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1126);
        }
        if ("".equals(data.getString("IDVALUE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1127);
        }
        if ("".equals(data.getString("BIZ_TYPE_CODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1128);
        }
        if ("".equals(data.getString("PASSWD", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1129);
        }
        if ("".equals(data.getString("OPR_NUMB", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1130);
        }
    }

}
