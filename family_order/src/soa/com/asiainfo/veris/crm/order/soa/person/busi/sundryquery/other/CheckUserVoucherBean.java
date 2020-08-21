
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.WapException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.uservoucher.UserVoucherOutQry;

public class CheckUserVoucherBean extends CSBizBean
{

    public boolean checkUserVoucher(IData data) throws Exception
    {
        boolean isOK = false;
        ArrayList list = new ArrayList();
        list.add("SERIAL_NUMBER"); // 标识号码
        list.add("CREDENCE_NO"); // 凭证号码
        list.add("SESSION_ID"); // SESSIONID
        list.add("IDTYPE"); // 标识类型
        for (int i = 0; i < list.size(); i++)
        {
            if ("".equals(data.getString(list.get(i).toString())) || null == data.getString(list.get(i).toString()))
            {
                CSAppException.apperr(WapException.CRM_WAP_700001, list.get(i).toString());
            }
        }
        // data.put("SESSION_ID", data.getString("SESSIONID"));
        // data.put("SERIAL_NUMBER", data.getString("IDITEMRANGE"));

        // 校验必填参数是否存在，如果必传参数不存在则直接返回错误信息
        // if(null == data.get("SESSION_ID") || "".equals(data.get("SESSION_ID"))
        // || null == data.get("SERIAL_NUMBER") || "".equals(data.get("SERIAL_NUMBER")))
        // {
        // common.error("880002:缺少SESSION_ID或SERIAL_NUMBER参数或值！");
        // }

        // 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息
        data.put("CREDENCE_NO_INTF", data.getString("CREDENCE_NO"));
        IDataset res = UserVoucherOutQry.getUserVoucherInfo(data); // 查询该用户wap凭证信息
        if (null == res || res.size() < 1)
        {
            CSAppException.apperr(WapException.CRM_WAP_700002);
        }
        String credenceNo = ((IData) res.get(0)).getString("CREDENCE_NO"); // 用户身份凭证号

        if ("".equals(credenceNo) || null == credenceNo)
        {
            CSAppException.apperr(WapException.CRM_WAP_700003);
        }
        String inCredenceNo = data.getString("CREDENCE_NO_INTF");
        if (inCredenceNo.equals(credenceNo) || inCredenceNo == credenceNo)
        {
            isOK = true;
        }
        else
        {
            CSAppException.apperr(WapException.CRM_WAP_700004);
        }
        return isOK;
    }
}
