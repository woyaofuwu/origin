
package com.asiainfo.veris.crm.order.soa.script.rule.brand;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsUserGroupBrand extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsUserGroupBrand.class);

    /**
     * 判断是否集团品牌
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsUserGroupBrand() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strParaCode1 = ruleParam.getString(databus, "PARA_CODE1");

        /* 获取业务台账，用户资料信息 */
        String strParamCode = ruleParam.getString(databus, "BRAND_CODE");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("PARA_CODE1", strParaCode1);
        param.put("PARAM_CODE", strParamCode);
        param.put("X_CONN_DB_CODE", Route.CONN_CRM_CEN);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "IsUserGroupBrand", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsUserGroupBrand() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
