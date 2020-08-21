
package com.asiainfo.veris.crm.order.soa.person.busi.usevoucher;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.uservoucher.UserVoucherOutQry;

public class UserVoucherOutBean extends CSBizBean
{

    /**
     * 鉴权表更新
     * 
     * @param pd
     * @param inparams
     * @return EPARCHY_CODE
     * @throws Exception
     * @author huangwei
     */
    public IData userVoucherHanghalf(IData data) throws Exception
    {
        IData result = new DataMap();
        if (data.getString("IDTYPE", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001:IDTYPE标识类型字段没传！");
        }

        if (data.getString("IDITEMRANGE", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001:IDITEMRANGE标识号码字段没传！");
        }

        if (data.getString("IDENTCODE", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001:IDENTCODE用户身份凭证字段没传！");
        }

        if (data.getString("SESSIONID", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001:一级WAP SessionID字段没传！");
        }

        if (data.getString("OPRNUMB", "").equals(""))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700001:OPRNUMB操作流水号字段没传！");
        }

        int svc;
        IData param = new DataMap();
        param.put("ID_TYPE", data.getString("IDTYPE"));
        param.put("SERIAL_NUMBER", data.getString("IDITEMRANGE"));
        param.put("CREDENCE_NO", data.getString("IDENTCODE"));
        param.put("SESSION_ID", data.getString("SESSIONID"));
        svc = UserVoucherOutQry.updateSessionTime(param);
        if (svc <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_13, "700102", "鉴权延时失败，该用户身份凭证已经失效或不存在，需重新进行鉴权登录!");
        }
        result.put("X_RESULTCODE", "00");
        result.put("X_RESULTINFO", "OK");

        return result;
    }
}
