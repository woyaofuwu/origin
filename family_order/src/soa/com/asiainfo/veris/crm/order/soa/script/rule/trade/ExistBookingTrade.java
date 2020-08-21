
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistBookingTrade extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistBookingTrade.class);

    /**
     * 存在4天以内特定渠道，特定业务历史工单
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistAdvPayRelation() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */

        /* 开始逻辑规则校验 */

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistAdvPayRelation() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
