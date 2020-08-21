
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTXSCRESTRADEID extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTXSCRESTRADEID.class);

    /**
     * 查询串号是否在存量串码库中存在
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTXSCRESTRADEID() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取规则配置信息 */
        String strTradeEparchyCode = ruleParam.getString(databus, "TRADE_EPARCHY_CODE");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE_SALE_GOODS");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();
            IData param = new DataMap();
            param.put("RES_CODE", trade.getString("RES_CODE"));
            bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsTXSCRESTRADEID", param);
            if (bResult)
            {
                break;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTXSCRESTRADEID() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
