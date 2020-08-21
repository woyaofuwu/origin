
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistEparchyDiscnt extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistEparchyDiscnt.class);

    /**
     * 检查是否选择非本地州的优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistEparchyDiscnt() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strUserId = databus.getString("USER_ID");
        String strEparchyCode = databus.getString("EPARCHY_CODE");

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");

        /* 开始逻辑规则校验 */
        int iCountTradeDiscnt = listTradeDiscnt.size();
        for (int iTradeDiscnt = 0; iTradeDiscnt < iCountTradeDiscnt; iTradeDiscnt++)
        {
            if (strUserId.equals(listTradeDiscnt.get(iTradeDiscnt, "USER_ID")))
            {
                IDataset listDiscnt = StaticUtil.getList(getVisit(), "TD_B_DISCNT", "DISCNT_CODE", "RSRV_STR2", "DISCNT_CODE", (String) listTradeDiscnt.get(iTradeDiscnt, "DSICNT_CODE"));
                int iCountDiscnt = listDiscnt.size();
                for (int iDiscnt = 0; iDiscnt < iCountDiscnt; iDiscnt++)
                {
                    if (!listDiscnt.get(iDiscnt, "RSRV_STR2", "ZZZZ").equals("ZZZZ") && !strEparchyCode.equals(listDiscnt.get(iDiscnt, "RSRV_STR2")))
                    {
                        if (logger.isDebugEnabled())
                            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistEparchyDiscnt() " + true + "<<<<<<<<<<<<<<<<<<<");
                        return true;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistEparchyDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
