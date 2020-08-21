
package com.asiainfo.veris.crm.order.soa.script.rule.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class DelSvcExistsState extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(DelSvcExistsState.class);

    /**
     * 判断用户是否删除了某个状态的服务
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 DelSvcExistsState() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;

        /* 获取规则配置信息 */
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");
        String strSvcStateCode = ruleParam.getString(databus, "STATE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvc = databus.getDataset("TF_B_TRADE_SVC");
        IDataset listUserSvcState = databus.getDataset("TF_F_USER_SVCSTATE");

        /* 开始逻辑规则校验 */
        int iCountTradeSvc = listTradeSvc.size();
        int iCountUserSvcState = listUserSvcState.size();
        for (int iTradeSvc = 0; iTradeSvc < iCountTradeSvc; iTradeSvc++)
        {
            if (listTradeSvc.get(iTradeSvc, "SERVICE_ID").equals(strSvcId) && "1".equals(listTradeSvc.get(iTradeSvc, "MODIFY_TAG")))
            {
                for (int iUserSvcState = 0; iUserSvcState < iCountUserSvcState; iUserSvcState++)
                {
                    if (listUserSvcState.get(iUserSvcState, "SERVICE_ID").equals(strSvcId) && listUserSvcState.get(iUserSvcState, "STATE_CODE").equals(strSvcStateCode))
                    {
                        if (logger.isDebugEnabled())
                            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DelSvcExistsState() " + true + "<<<<<<<<<<<<<<<<<<<");
                        return true;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 DelSvcExistsState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
