
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
 * 2.3.18.param_attr = 1586 >> 统一付费主号办理了para_code1活动副号方可办理param_code活动
 * 
 * @author Mr.Z 1586参数废除 PARAM_PRODUCT_ID 入参，用户已经办理的活动
 */
public class CheckPayMainUserActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 7408597208732300002L;

    private static Logger logger = Logger.getLogger(CheckPayMainUserActive.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPayMainUserActive() >>>>>>>>>>>>>>>>>>");
        }

        String paramProductId = ruleParam.getString(databus, "PARAM_PRODUCT_ID");
        String userId = databus.getString("USER_ID");

        IDataset mainActives = BreQry.getPayMainActives(userId, paramProductId);

        if (IDataUtil.isEmpty(mainActives))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20157, "为其统付的主号未办理[" + paramProductId + "]活动,该用户不能办理！");
            return false;
        }
        else
        {
            String productId = databus.getString("PRODUCT_ID");
            IDataset memberActives = BreQry.getPayMemberActives(userId, productId);

            if (IDataUtil.isNotEmpty(memberActives))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20157, "其统付关系下已有成员办理过该活动，用户不能办理！");
                return false;
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPayMainUserActive() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
