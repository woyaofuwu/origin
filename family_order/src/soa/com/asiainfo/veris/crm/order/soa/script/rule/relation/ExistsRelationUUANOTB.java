
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsRelationUUANOTB extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsRelationUUANOTB.class);

    /**
     * 判断用户是否其他UU关系的子用户
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsRelationUUANOTB() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strRoleCodeB = ruleParam.getString(databus, "ROLE_CODE_B");

        /* 获取业务台账，用户资料信息 */
        String strUserIdA = databus.getString("USER_ID");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("USER_ID_A", strUserIdA);
        param.put("RELATION_TYPE_CODE", strRelationTypeCode);
        param.put("ROLE_CODE_B", strRoleCodeB);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsRelationUUANOTB", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsRelationUUANOTB() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
