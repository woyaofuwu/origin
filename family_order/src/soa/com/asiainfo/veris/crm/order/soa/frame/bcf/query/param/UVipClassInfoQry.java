
package com.asiainfo.veris.crm.order.soa.frame.bcf.query.param;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class UVipClassInfoQry
{
    /**
     * 根据大客户等级编码查询大客户等级名称
     * 
     * @param vipTypeCode
     * @param VipClassId
     * @return
     * @throws Exception
     */
    public static String getVipClassNameByVipTypeCodeClassId(String vipTypeCode, String VipClassId) throws Exception
    {
        return StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_VIPCLASS", new String[]
        { "VIP_TYPE_CODE", "CLASS_ID" }, "CLASS_NAME", new String[]
        { vipTypeCode, VipClassId });
    }
}
