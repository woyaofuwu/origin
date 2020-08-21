
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;

/**
 * 用户信用度等级规则 td_bre_parameter 入参 CREDIT_CLASS 目前只有一处用到， 填 0 --------------------用到的产品 69900803 需要和入网时间的规则一起使用， 增加
 * 69900805 CheckUserOpenDate
 * 
 * @author Mr.Z
 */
public class CheckUserCreditClass extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 3743856634529668244L;

    private static Logger logger = Logger.getLogger(CheckUserCreditClass.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserCreditClass() >>>>>>>>>>>>>>>>>>");
        }

        String creditClass = ruleParam.getString(databus, "CREDIT_CLASS");
        String userCreditClass = SaleActiveUtil.queryUserCreditClass(databus.getString("USER_ID"));

        if (creditClass.equals(userCreditClass))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20047, "用户信用度为" + userCreditClass + "星级，不能继续办理！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckUserCreditClass() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
