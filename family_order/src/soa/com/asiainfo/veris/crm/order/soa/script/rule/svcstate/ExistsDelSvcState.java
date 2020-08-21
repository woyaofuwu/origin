
package com.asiainfo.veris.crm.order.soa.script.rule.svcstate;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsDelSvcState extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsDelSvcState.class);

    /**
     * 判断是否删除了某个服务状态
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsDelSvcState() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strSvcId = ruleParam.getString(databus, "SERVICE_ID");
        String strStateCode = ruleParam.getString(databus, "STATE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeSvcState = databus.getDataset("TF_B_TRADE_SVCSTATE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeSvcState.iterator(); iter.hasNext();)
        {
            IData svcstate = (IData) iter.next();

            if (strSvcId.equals(svcstate.getString("SERVICE_ID")) && strStateCode.equals(svcstate.getString("STATE_CODE")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsDelSvcState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
