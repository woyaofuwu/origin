
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class UUserTypeInfoQry
{
    /**
     * 根据用户类型编码查询用户类型名称
     * 
     * @param userTypeCode
     * @return
     * @throws Exception
     */
    public static String getUserTypeByUserTypeCode(String userTypeCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_USERTYPE", "USER_TYPE_CODE", "USER_TYPE", userTypeCode);
    }

}
