
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.payment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserPayMentInfoQry
{
    // 查询用户是否定制或定制过手机缴费通
    public static IDataset queryUserHavePayMent(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SERNUMBER_PAYMENT_INFO", param);
    }
}
