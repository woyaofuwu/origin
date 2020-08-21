
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: GPRS服务和GPRS优惠必须同时存在！
 * @author: xiaocl
 */

/*
 * SELECT SUM(recordnum) recordcount FROM (SELECT COUNT(*) recordnum FROM tf_f_user_svc a WHERE user_id =
 * TO_NUMBER(:USER_ID) AND partition_id = MOD(TO_NUMBER(:USER_ID),10000) AND service_id = :SERVICE_ID AND end_date >
 * SYSDATE AND NOT EXISTS (SELECT 1 FROM tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND user_id = TO_NUMBER(:USER_ID) AND decode(modify_tag, '4', '0','5','1', modify_tag) = '1'
 * AND service_id = a.service_id) UNION ALL SELECT COUNT(*) recordnum FROM tf_b_trade_svc b WHERE trade_id =
 * TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND user_id = TO_NUMBER(:USER_ID) AND service_id =
 * :SERVICE_ID AND decode(modify_tag, '4', '0','5','1', modify_tag) in('0','U'))
 */
public class ExistsAllUserSvc extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsAllUserSvc.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAllUserSvc() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsTwo = false; // 设置第二逻辑点

        // A，用户必须存在有效GPRS服务资料。且在服务台账里面没有相关的删除服务台账！
        IDataset listUserSvc = databus.getDataset("TF_F_USER_SVC");
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");

        for (int iListTradeSvc = 0, iASize = listTradeSvc.size(); iListTradeSvc < iASize; iListTradeSvc++)
        {
            IData tradeSvcData = listTradeSvc.getData(iListTradeSvc);
            String strModifyTag = tradeSvcData.getString("MODIFY_TAG");
            if (strModifyTag.equals("4"))
            {
                strModifyTag = "0";
            }
            else if (strModifyTag.equals("5"))
            {
                strModifyTag = "1";
            }
            for (int iListUserSvc = 0, iBSize = listUserSvc.size(); iListUserSvc < iBSize; iListUserSvc++)
            {
                if (!listTradeSvc.getData(iListTradeSvc).getString("SERVICE_ID").equals(listUserSvc.getData(iListUserSvc).getString("SERVICE_ID")) && strModifyTag.equals("1"))
                {
                    bExistsOne = true;
                    continue;
                }
                else
                {
                	bExistsOne = false;
                    break;
                }
            }
            if (!bExistsOne)
            {
                break;
            }
        }

        // B，用户存在GPRS服务台账
        for (int iListTradeSvc = 0, iCSize = listTradeSvc.size(); iListTradeSvc < iCSize; iListTradeSvc++)
        {
            IData tradeSvcData = listTradeSvc.getData(iListTradeSvc);
            String strModifyTag = tradeSvcData.getString("MODIFY_TAG");
            if (strModifyTag.equals("4"))
            {
                strModifyTag = "0";
            }
            else if (strModifyTag.equals("5"))
            {
                strModifyTag = "1";
            }
            if (strModifyTag.equals("0") || strModifyTag.equals("U"))
            {
                bExistsTwo = true;
                break;
            }
        }
        if (bExistsOne || bExistsTwo)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAllUserSvc() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
