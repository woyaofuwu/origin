
package com.asiainfo.veris.crm.order.soa.script.rule.right;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBCheckBlackList extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCheckBlackList.class);

    /**
     * 判断客户名称是否包涵特殊字符
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBlackList() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        int iTradeTypeCode = databus.getInt("TRADE_TYPE_CODE");

        /* 开始逻辑规则校验 */

        if ((StringUtils.isBlank(databus.getString("X_CHOICE_TAG", "")) || "1".equals(databus.getString("X_CHOICE_TAG", "")))
                && (iTradeTypeCode == 1020 || iTradeTypeCode == 1025 || iTradeTypeCode == 1028 || iTradeTypeCode == 1029 || iTradeTypeCode == 1030 || iTradeTypeCode == 1040 || iTradeTypeCode == 1050 || iTradeTypeCode == 1060
                        || iTradeTypeCode == 5010 || iTradeTypeCode == 5011 || iTradeTypeCode == 5012 || iTradeTypeCode == 5013 || iTradeTypeCode == 5014 || iTradeTypeCode == 5015 || iTradeTypeCode == 5016 || iTradeTypeCode == 5017 || iTradeTypeCode == 1065))
        {
            if (StaffPrivUtil.isFuncDataPriv(databus.getString("CANCEL_STAFF_ID"), "SYSGroupTradeCancel"))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_FORCE_EXIT, -1, "您无权取消集团业务!");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCheckBlackList() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
