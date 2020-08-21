
package com.asiainfo.veris.crm.order.soa.script.rule.vip;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class ExistsAttachCardCountByMainCardVipClass extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsAttachCardCountByMainCardVipClass.class);

    /**
     * 根据主卡的VIP类别判断能够绑定附卡数量
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAttachCardCountByMainCardVipClass() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iTotal = 0;

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");
        String strVipTypeCode = ruleParam.getString(databus, "VIP_TYPE_CODE");
        int iAttachCardCount = ruleParam.getInt(databus, "ATTACH_CARD_COUNT");
        String strVipTypeName = ruleParam.getString(databus, "VIP_TYPE_NAME");

        /* 获取业务台账，用户资料信息 */
        IDataset listCustVip = databus.getDataset("TF_F_CUST_VIP");

        /* 开始逻辑规则校验 */
        if (listCustVip.size() > 0)
        {
            IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");
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

            if (strVipTypeCode.equals(listCustVip.getData(0).getString("VIP_TYPE_CODE")) && iTotal > iAttachCardCount)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201670, "#业务后特殊限制表判断：用户的大客户类型是【" + strVipTypeName + "】，只可以绑定【" + iAttachCardCount + "】个副卡！");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAttachCardCountByMainCardVipClass() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
