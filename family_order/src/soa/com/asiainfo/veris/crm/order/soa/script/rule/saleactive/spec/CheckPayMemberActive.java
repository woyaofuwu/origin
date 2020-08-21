
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/**
 * 2.3.16.param_attr = 1588 >> 统一付费成员办理para_code2活动才可办理para_code1活动
 * 
 * @author Mr.Z 1588参数废除 PARAM_PRODUCT_ID 入参，用户已经办理的活动
 */
public class CheckPayMemberActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 7583367082818893417L;

    private static Logger logger = Logger.getLogger(CheckPayMemberActive.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPayMemberActive() >>>>>>>>>>>>>>>>>>");
        }

        String paramProductId = ruleParam.getString(databus, "PARAM_PRODUCT_ID");
        String userId = databus.getString("USER_ID");

        IDataset memberActives = BreQry.getPayMemberBActives(userId, paramProductId);

        if (IDataUtil.isEmpty(memberActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20157, "统一付费成员须办理[" + paramProductId + "]活动才可办理此活动!");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPayMemberActive() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
