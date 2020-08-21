
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 全球通商务服务之移动秘书免费(921)依赖的优惠不存在，请取消全球通商务服务之秘书免费
 * @author: xiaocl
 */
/*
 * SELECT count(*) recordcount FROM tf_f_user_discnt a WHERE a.User_Id = :USER_ID AND a.discnt_code = :DISCNT_CODE AND
 * SYSDATE BETWEEN a.start_date AND a.end_date AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt a WHERE a.trade_id =
 * :TRADE_ID AND a.accept_month = :ACCEPT_MONTH AND a.discnt_code = :DISCNT_CODE AND a.modify_tag = :MODIFY_TAG)
 */
public class existsdiscnt921 extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(existsdiscnt921.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 existsdiscnt921() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsTwo = false; // 设置第二逻辑点
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT");
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        // A 不存在921优惠删除台账
        for (int iListTradeDiscnt = 0, iASize = listTradeDiscnt.size(); iListTradeDiscnt < iASize; iListTradeDiscnt++)
        {
            if (listTradeDiscnt.getData(iListTradeDiscnt).getString("MODIFY_TAG").equals(strModifyTag) && listTradeDiscnt.getData(iListTradeDiscnt).getString("DISCNT_CODE").equals(strDiscntCode))
            {
                bExistsOne = true;
                break;
            }
        }
        // B 用户现有资料存在921 有效资料
        for (int iListUserDiscnt = 0, iBSize = listUserDiscnt.size(); iListUserDiscnt < iBSize; iListUserDiscnt++)
        {
            if (listUserDiscnt.getData(iListUserDiscnt).getString("DISCNT_CODE").equals(strDiscntCode))
            {
                bExistsTwo = true;
                break;
            }
        }
        if (bExistsOne && bExistsTwo)
        {
            bResult = true;
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 existsdiscnt921() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
