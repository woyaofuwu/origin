
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 全球通商务服务之移动秘书免费(921)依赖的优惠不存在，请取消全球通商务服务之秘书免费
 * @author: xiaocl 仅适应于当前用户有优惠(921)依赖的优惠，且当前正准备删除这些被依赖的优惠的场景。且没有另外新增或者继承这些被依赖的优惠。
 */
/*
 * SELECT count(*) recordcount FROM tf_f_user_discnt a WHERE a.User_Id = :USER_ID AND a.discnt_code IN (SELECT
 * param_code FROM td_s_commpara WHERE param_attr = :PARAM_ATTR AND SYSDATE BETWEEN start_date AND end_date) AND SYSDATE
 * BETWEEN a.start_date AND a.end_date AND EXISTS (SELECT 1 FROM tf_b_trade_discnt b WHERE b.trade_id = :TRADE_ID AND
 * b.accept_month = :ACCEPT_MONTH AND b.discnt_code = a.discnt_code AND b.modify_tag = '1') AND NOT EXISTS (SELECT 1
 * FROM tf_b_trade_discnt c WHERE c.trade_id = :TRADE_ID AND c.accept_month = :ACCEPT_MONTH AND c.modify_tag IN ('0',
 * 'U') AND c.discnt_code IN (SELECT param_code FROM td_s_commpara WHERE param_attr = :PARAM_ATTR AND SYSDATE BETWEEN
 * start_date AND end_date))
 */

public class existsdiscnt232 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(existsdiscnt232.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 existsdiscnt232() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsTwo = false; // 设置第二逻辑点
        boolean bExistsThree = false;
        // TODO
        int strParamAttr = ruleParam.getInt(databus, "PARAM_ATTR");
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
        IDataset listComparaDiscnt = BreQryForCommparaOrTag.getCommpara("CSM", strParamAttr, strEparchyCode);

        // A 当前业务没有对921依赖的优惠套餐(比如全球通138超值套餐)进行变更或者删除
        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
        // "201119","[bExistsTwo]"+bExistsTwo+"[bExistsThree]"+bExistsThree+"[bExistsOne]"+bExistsOne);
        for (int iListTradeDiscnt = 0, iASzie = listTradeDiscnt.size(); iListTradeDiscnt < iASzie; iListTradeDiscnt++)
        {
            IData tradeDiscnt = listTradeDiscnt.getData(iListTradeDiscnt);
            String strModify = tradeDiscnt.getString("MODIFY_TAG");
            for (int i = 0, iBSize = listComparaDiscnt.size(); i < iBSize; i++)
            {
                if ((strModify.equals("0") || strModify.equals("U")) && listTradeDiscnt.getData(iListTradeDiscnt).getString("DISCNT_CODE").equals(listComparaDiscnt.getData(i).getString("PARAM_CODE")))
                {
                    bExistsOne = true;
                    break;
                }
            }
            if (bExistsOne)
            {
                break;
            }
        }

        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
        // "201119","listUserDiscnt"+listUserDiscnt.toString());
        // B 用户现有资料存在921 有效资料
        for (int iListUserDiscnt = 0, iCSize = listUserDiscnt.size(); iListUserDiscnt < iCSize; iListUserDiscnt++)
        {
            String strDiscntFor = listUserDiscnt.getData(iListUserDiscnt).getString("DISCNT_CODE");
            for (int i = 0, iBSize = listComparaDiscnt.size(); i < iBSize; i++)
            {
                if (strDiscntFor.equals(listComparaDiscnt.getData(i).getString("PARAM_CODE")))
                {
                    bExistsTwo = true;
                    break;
                }
            }
            for (int iListTradeDiscnt = 0, iDSize = listTradeDiscnt.size(); iListTradeDiscnt < iDSize; iListTradeDiscnt++)
            {
                if (listTradeDiscnt.getData(iListTradeDiscnt).getString("DISCNT_CODE").equals(strDiscntFor) && listTradeDiscnt.getData(iListTradeDiscnt).getString("MODIFY_TAG").equals("1"))
                {
                    bExistsThree = true;
                    break;
                }
            }
            if (bExistsTwo && bExistsThree)
            {
                break;
            }
        }

        // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR,
        // "201119","[bExistsTwo]"+bExistsTwo+"[bExistsThree]"+bExistsThree+"[bExistsOne]"+bExistsOne);
        if (!bExistsOne && bExistsTwo && bExistsThree)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 existsdiscnt232() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;

    }
}
