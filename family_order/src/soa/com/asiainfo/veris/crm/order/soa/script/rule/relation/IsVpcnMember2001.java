
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsVpcnMember2001 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsVpcnMember2001.class);

    /**
     * 判断用户是否VPCN成员用户
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsVpcnMember2001() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");

        /* 获取业务台账，用户资料信息 */
        String strUserId = databus.getString("USER_ID");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("UESR_ID", strUserId);
        param.put("PARAM_CODE", strParamCode);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "is_vpcn_member_2001", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsVpcnMember2001() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
