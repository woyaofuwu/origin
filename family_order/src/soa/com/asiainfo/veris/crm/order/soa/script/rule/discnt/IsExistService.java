
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTimeUtil;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 号码不支持语音服务!
 * @author: xiaocl
 */
/*
 * SELECT COUNT(*) recordcount from (SELECT 1 FROM tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month
 * = TO_NUMBER(:ACCEPT_MONTH) AND service_id = TO_NUMBER(:SERVICE_ID) AND modify_tag <> '1' AND end_date > sysdate UNION
 * SELECT 1 FROM tf_f_user_svc WHERE user_id = TO_NUMBER(:USER_ID) AND service_id = TO_NUMBER(:SERVICE_ID) AND end_date
 * > sysdate) a WHERE NOT EXISTS (SELECT 1 FROM tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month =
 * TO_NUMBER(:ACCEPT_MONTH) AND service_id = TO_NUMBER(:SERVICE_ID) AND modify_tag = '1') AND NOT EXISTS (SELECT 1 FROM
 * tf_b_trade_svc WHERE trade_id = TO_NUMBER(:TRADE_ID) AND accept_month = TO_NUMBER(:ACCEPT_MONTH) AND service_id =
 * TO_NUMBER(:SERVICE_ID) AND modify_tag = '2')
 */
public class IsExistService extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(IsExistService.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsExistService() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        boolean bExistsOne = false; // 设置第一逻辑点
        boolean bExistsTwo = false; // 设置第二逻辑点
        IDataset listUserSvc = databus.getDataset("TF_F_USER_SVC");
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        String strSvc = ruleParam.getString(databus, "SERVICE_ID");
        String strSysdate = BreTimeUtil.getCurDate(databus);

        for (int ilistUserSvc = 0, iSzie = listUserSvc.size(); ilistUserSvc < iSzie; ilistUserSvc++)
        {
            IData userSvc = listUserSvc.getData(ilistUserSvc);
            if (userSvc.getString("END_DATE").compareTo(strSysdate) > 0 && userSvc.getString("SERVICE_ID").equals(strSvc))
            {
                bExistsOne = true;
            }
        }

        if (bExistsOne)
        {
            for (int ilistTradeSvc = 0, iASize = listTradeSvc.size(); ilistTradeSvc < iASize; ilistTradeSvc++)
            {
                IData tradeSvc = listTradeSvc.getData(ilistTradeSvc);
                if (tradeSvc.getString("SERVICE_ID").equals(strSvc) && !tradeSvc.getString("MODIFY_TAG").equals("1") && tradeSvc.getString("END_DATE").compareTo(strSysdate) > 0)
                {
                    bExistsTwo = true;
                    break;
                }
            }
        }

        if (bExistsTwo)
        {
            for (int ilistTradeSvc = 0, iCSize = listTradeSvc.size(); ilistTradeSvc < iCSize; ilistTradeSvc++)
            {
                IData data = listTradeSvc.getData(ilistTradeSvc);
                if (data.getString("SERVICE_ID").equals(strSvc) && (data.getString("MODIFY_TAG").equals("1") || data.getString("MODIFY_TAG").equals("2")) && data.getString("END_DATE").compareTo(strSysdate) > 0)
                {
                    bResult = false;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsExistService() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
