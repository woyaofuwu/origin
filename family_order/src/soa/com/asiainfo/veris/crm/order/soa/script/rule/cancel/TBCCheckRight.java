
package com.asiainfo.veris.crm.order.soa.script.rule.cancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class TBCCheckRight extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCCheckRight.class);

    /**
     * 判断用户是否有返销权限
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCCheckRight() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 开始逻辑规则校验 */
        int iTradeTypeCode = databus.getInt("TRADE_TYPE_CODE");

        if (iTradeTypeCode == 1020 || iTradeTypeCode == 1025 || iTradeTypeCode == 1028 || iTradeTypeCode == 1029 || iTradeTypeCode == 1030 || iTradeTypeCode == 1040 || iTradeTypeCode == 1050 || iTradeTypeCode == 1060 || iTradeTypeCode == 5010
                || iTradeTypeCode == 5011 || iTradeTypeCode == 5012 || iTradeTypeCode == 5013 || iTradeTypeCode == 5014 || iTradeTypeCode == 5015 || iTradeTypeCode == 5016 || iTradeTypeCode == 5017 || iTradeTypeCode == 1065)
        {
            if (!StaffPrivUtil.isFuncDataPriv(databus.getString("CANCEL_STAFF_ID"), "SYSGroupTradeCancel"))
            {
                StringBuilder errInfo = new StringBuilder("业务取消校验[取消权限校验]：您无权取消集团业务!");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751121, errInfo.toString());
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCCheckRight() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
