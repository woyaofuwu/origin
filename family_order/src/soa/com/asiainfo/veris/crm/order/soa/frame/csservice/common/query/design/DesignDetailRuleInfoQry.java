
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.design;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DesignDetailRuleInfoQry
{
    /**
     * 查询规则明细信息
     * 
     * @param ruleId
     * @return
     * @throws Exception
     */
    public static IDataset qryDesignDetailRuleByRuleId(String ruleId) throws Exception
    {
        IData param = new DataMap();

        param.put("RULE_ID", ruleId);

        return Dao.qryByCode("TD_B_GROUP_DESIGN_DETAILRULE", "SEL_BY_RULEID", param, Route.CONN_CRM_CEN);
    }
}
