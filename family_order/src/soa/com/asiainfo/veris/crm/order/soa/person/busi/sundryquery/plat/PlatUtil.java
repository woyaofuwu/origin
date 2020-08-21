
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class PlatUtil extends CSBizBean
{

    public static String getOperNumb(String trade_id) throws Exception
    {
        String timeStr = SysDateMgr.getSysDate("yyyyMMddHHmmssS");
        String prov_code = getProvCode();
        return prov_code.substring(prov_code.length() - 3) + "BIP2B783" + timeStr.substring(0, timeStr.length() - 1) + trade_id.substring(trade_id.length() - 5);
    }

    public static String getProvCode() throws Exception
    {
        String provCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode() });

        if (provCode == null || provCode.length() == 0)
        {
            // common.error("查询省代码无资料！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询省代码无资料！");
        }
        return provCode;
    }

}
