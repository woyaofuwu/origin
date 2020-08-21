
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class UPayModeInfoQry
{
    /**
     * 根据付费方式编码查询付费方法名称
     * 
     * @param payModeCode
     * @return
     * @throws Exception
     */
    public static String getPayModeNameByPayModeCode(String payModeCode) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_PAYMODE", "PAY_MODE_CODE", "PAY_MODE", payModeCode);
    }
}
