
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsAttachCardCountByMainCardDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsAttachCardCountByMainCardDiscnt.class);

    /**
     * 根据主卡的优惠判断能够绑定附卡数量
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAttachCardCountByMainCardDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iTotal = 0;

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        int iAttachCardCount = ruleParam.getInt(databus, "ATTACH_CARD_COUNT");
        String strDiscntName = ruleParam.getString(databus, "DISCNT_NAME");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if (strRelationTypeCode.equals(tradeRelation.getString("RELATION_TYPE_CODE")))
            {
                if ("0".equals(tradeRelation.getString("MODIFY_TAG")) || "2".equals(tradeRelation.getString("MODIFY_TAG")))
                {
                    iTotal++;
                }
                else if ("1".equals(tradeRelation.getString("MODIFY_TAG")))
                {
                    iTotal--;
                }
            }
        }

        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");

        for (Iterator iterUserDiscnt = listUserDiscnt.iterator(); iterUserDiscnt.hasNext();)
        {
            IData userDiscnt = (IData) iterUserDiscnt.next();

            if (strDiscntCode.equals(userDiscnt.getString("DISCNT_CODE")) && iTotal > iAttachCardCount)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：用户有【" + strDiscntName + "】优惠，只可以绑定【" + iAttachCardCount + "】个副卡！");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAttachCardCountByMainCardDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
