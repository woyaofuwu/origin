
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;

/**
 * 2.3.15.param_attr = 1526 >> 亲亲网成员数目为para_code2才可办理para_code1活动 规则入参参数中需要传入数目的限制 td_bre_parameter relation_type_code
 * limit_number
 * 
 * @author Mr.Z
 */
public class CheckQqwNumber extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -4969347844723161148L;

    private static Logger logger = Logger.getLogger(CheckQqwNumber.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckQqwNumber() >>>>>>>>>>>>>>>>>>");
        }

        String relationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String limitNumber = ruleParam.getString(databus, "LIMIT_NUMBER");
        String userId = databus.getString("USER_ID");

        int number = CParamQry.getRelationMemberNumber(userId, relationTypeCode);

        if (number < Integer.parseInt(limitNumber))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062420, "该号码组建的亲亲网成员数量不足！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckQqwNumber() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
