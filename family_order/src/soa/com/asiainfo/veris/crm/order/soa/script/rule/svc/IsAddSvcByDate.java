
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 你在6个 月以内办理过移动气象通，不能再次办理。
 * @author: xiaocl
 */
/*
 * SELECT count(1) recordcount FROM tf_f_user_svc a WHERE a.partition_id = mod(to_number(:USER_ID),10000) and a.user_id
 * = to_number(:USER_ID) and a.service_id = to_number(:SERVICE_ID) and EXISTS (SELECT 1 FROM tf_b_trade_svc where
 * trade_id=TO_NUMBER(:TRADE_ID) AND accept_month=TO_NUMBER(:ACCEPT_MONTH) AND service_id=a.service_id --230 AND
 * (modify_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')----0 ) and SYSDATE < DECODE(:TYPE, '0',a.start_date+:NUM, --天
 * '1',trunc(a.start_date)+:NUM, --自然天 '2',ADD_MONTHS(a.start_date,:NUM), --月
 * '3',trunc(ADD_MONTHS(a.start_date,:NUM),'mm'), --自然月 '4',ADD_MONTHS(a.start_date,:NUM*12), --年
 * '5',trunc(ADD_MONTHS(a.start_date,:NUM*12),'yy'), --自然年 SYSDATE )
 */
public class IsAddSvcByDate extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(IsAddSvcByDate.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsAddSvcByDate() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String paraModify = ruleParam.getString(databus, "MODIFY_TAG");
        String paraType = ruleParam.getString(databus, "TYPE");
        int paraNum = Integer.parseInt(ruleParam.getString(databus, "NUM"));
        String paraSvc = ruleParam.getString(databus, "SERVICE_ID");
        IDataset listUserSvc = databus.getDataset("TF_F_USER_SVC");
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        for (int i = 0, iSize = listUserSvc.size(); i < iSize; i++)
        {
            IData userData = listUserSvc.getData(i);
            String strSvc = userData.getString("SERVICE_ID");
            for (int j = 0, jSize = listTradeSvc.size(); j < jSize; j++)
            {
                IData tradesvcData = listTradeSvc.getData(j);
                if (SysDateMgr.getSysDate().compareTo(SysDateMgr.getAddMonthsNowday(paraNum, userData.getString("START_DATE"))) < 0 && "2".equals(paraType))
                {
                    if (strSvc.equals(paraSvc) && tradesvcData.getString("SERVICE_ID").equals(strSvc) && (tradesvcData.getString("MODIFY_TAG").equals(paraModify) || paraModify.equals("*")))
                    {
                        bResult = true;
                    }

                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsAddSvcByDate() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
