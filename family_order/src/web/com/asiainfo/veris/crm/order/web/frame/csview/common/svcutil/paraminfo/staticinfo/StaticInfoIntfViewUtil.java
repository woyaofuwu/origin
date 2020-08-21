
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.staticinfo;

import com.ailk.biz.util.StaticUtil;

public class StaticInfoIntfViewUtil
{
    /**
     * 通过付费类型编码查询付费类型名称
     * 
     * @param planTypeCode
     * @return
     * @throws Exception
     */
    public static String qryPlanTypeNameByPlanTypeCode(String planTypeCode) throws Exception
    {
        return StaticUtil.getStaticValue("PAYPLAN_PLANTYPE", planTypeCode);
    }

}
