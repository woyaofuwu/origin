
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeRes extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeRes.class);

    /**
     * 判断是否有 trade_res 台账
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeRes() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeRes = databus.getDataset("TF_B_TRADE_RES");

        /* 开始逻辑规则校验 */
        bResult = listTradeRes.size() > 0 ? true : false;

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeRes() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
