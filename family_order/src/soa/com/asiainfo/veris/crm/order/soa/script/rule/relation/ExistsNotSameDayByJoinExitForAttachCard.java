
package com.asiainfo.veris.crm.order.soa.script.rule.relation;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForUser;

public class ExistsNotSameDayByJoinExitForAttachCard extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsNotSameDayByJoinExitForAttachCard.class);

    /**
     * 判断附卡是否在同一天加入退出某个用户关系RELATION_TYPE_CODE
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsNotSameDayByJoinExitForAttachCard() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRelationTypeCode = ruleParam.getString(databus, "RELATION_TYPE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iterTradeRelation = listTradeRelation.iterator(); iterTradeRelation.hasNext();)
        {
            IData tradeRelation = (IData) iterTradeRelation.next();

            if ("1".equals(tradeRelation.getString("MODIFY_TAG")) && "2".equals(tradeRelation.getString("ROLE_CODE_B")) && strRelationTypeCode.indexOf("|" + tradeRelation.getString("RELATION_TYPE_CODE") + "|") > -1)
            {
                IDataset listUserRelation = BreQryForUser.getUserRelationByUserIdBAndRelationTypeCode(tradeRelation.getString("USER_ID_B"), strRelationTypeCode);

                if (listUserRelation.size() > 0 && listUserRelation.getData(0).getString("START_DATE").substring(0, 10).equals(tradeRelation.getString("END_DATE").substring(0, 10)))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201643, "#业务后特殊限制表判断：副卡【" + tradeRelation.getString("SHORT_CODE") + "】当天绑定不可以当天取消！");
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsNotSameDayByJoinExitForAttachCard() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
