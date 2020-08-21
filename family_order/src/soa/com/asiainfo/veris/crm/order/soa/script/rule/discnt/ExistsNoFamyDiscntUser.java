
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsNoFamyDiscntUser extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsNoFamyDiscntUser.class);

    /**
     * 家庭网优惠校验
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsNoFamyDiscntUser() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTaf = ruleParam.getString(databus, "MODIFY_TAG");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRelation = databus.getDataset("TF_B_TRADE_RELATION");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeRelation.iterator(); iter.hasNext();)
        {
            IData tradeRelation = (IData) iter.next();

            if (strModifyTaf.equals(tradeRelation.getString("MODIFY_TAG")))
            {
                IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

                for (Iterator iterator = listTradeDiscnt.iterator(); iterator.hasNext();)
                {
                    IData tradeDiscnt = (IData) iterator.next();

                    {
                        if (tradeRelation.getString("MODIFY_TAG").equals(tradeDiscnt.getString("MODIFY_TAG")) && tradeRelation.getString("RELATION_TYPE_CODE").equals(tradeDiscnt.getString("RELATION_TYPE_CODE"))
                                && tradeRelation.getString("USER_ID_B").equals(tradeDiscnt.getString("USER_ID")))
                        {
                            bResult = true;
                            break;
                        }

                    }
                }
            }

            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsNoFamyDiscntUser() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
