
package com.asiainfo.veris.crm.order.soa.script.rule.score;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ScoreExchangeVipSnLimit extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ScoreExchangeVipSnLimit.class);

    /**
     * @author huangsl
     * @deprecated 判断是不是限制积分号码
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ScoreExchangeVipSnLimit() >>>>>>>>>>>>>>>>>>");
        boolean bResult = false;

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            IDataset results = UserInfoQry.qryUserScoreLimit(serialNumber);
            if (IDataUtil.isNotEmpty(results))
            {
                bResult = true;
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, "业务受理前条件判断：您的积分已作限制，仅能兑换VIP贵宾厅服务。本次办理不成功。");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ScoreExchangeVipSnLimit() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
