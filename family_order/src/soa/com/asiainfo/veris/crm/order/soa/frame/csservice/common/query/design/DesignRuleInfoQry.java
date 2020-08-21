
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.design;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DesignRuleInfoQry
{

    /**
     * 查询定制规则信息
     * 
     * @param productId
     * @param packageId
     * @param elementTypeCode
     * @param elementId
     * @return
     * @throws Exception
     */
    public static IDataset qryDesignRule(String productId, String packageId, String elementTypeCode, String elementId) throws Exception
    {
        IData param = new DataMap();

        param.put("PRODUCT_ID", productId);
        if(StringUtils.isNotEmpty(packageId) && !packageId.equals("-1") && !packageId.equals("0"))
        {
            param.put("PACKAGE_ID", packageId);
        }
        param.put("ELEMENT_TYPE_CODE", elementTypeCode);
        param.put("ELEMENT_ID", elementId);

        return Dao.qryByCode("TD_B_GROUP_DESIGN_RULE", "SEL_BY_PK", param, Route.CONN_CRM_CEN);
    }
}
