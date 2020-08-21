
package com.asiainfo.veris.crm.order.soa.script.rule.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsTheSameEparchy extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsTheSameEparchy.class);

    /**
	 * 
	 */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsTheSameEparchy() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        String strTradeCityCode = databus.getString("TRADE_CITY_CODE");
        String strTradeEparchyCode = databus.getString("TRADE_EPARCHY_CODE");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("CITY_CODE", strTradeCityCode);
        param.put("EPARCHY_CODE", strTradeEparchyCode);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "IsTheSameEparchy", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsTheSameEparchy() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
