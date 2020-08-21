
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.monitorinfo;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class SpecialBizQryDealInput
{
    /**
     * 查询条件参数处理
     * 
     * @param cond
     * @throws Exception
     */
    public static void condParamDeal(IData cond) throws Exception
    {
        if (cond.getString("QUERY_TYPE", "").equals(""))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2002);
        }
        else if (cond.getString("QUERY_TYPE", "").equals(PersonConst.QUERY_TYPE_NORMAL))
        {
            // 普通查询
            phoneOfCondCheck(cond.getDouble("BEGIN_SN"), cond.getDouble("END_SN")); // 号码效验

            cond.put("BEGIN_SN", "86" + cond.getString("BEGIN_SN"));
            cond.put("END_SN", "86" + cond.getString("END_SN"));
        }
        else if (cond.getString("QUERY_TYPE", "").equals(PersonConst.QUERY_TYPE_REPORT))
        {
            // 日报表查询
            cond.put("QUERY_DATE_B", cond.getString("QUERY_DATE") + " 00:00:00");
            cond.put("QUERY_DATE_E", cond.getString("QUERY_DATE") + " 23:59:59");

        }
        else if (cond.getString("QUERY_TYPE", "").equals(PersonConst.QUERY_TYPE_REPORT_BETWEEN))
        {
            // 时段表查询

            cond.put("START_DATE", cond.getString("START_DATE") + " 00:00:00");
            cond.put("END_DATE", cond.getString("END_DATE") + " 23:59:59");
        }
    }

    public static void phoneOfCondCheck(double start_serial_number, double end_serial_number) throws Exception
    {
        if (end_serial_number < start_serial_number)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2003);
        }

        if (end_serial_number - start_serial_number > 1000)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2004);
        }
    }

}
