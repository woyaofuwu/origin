
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class SEL_BY_TRADEID_OTHER extends BreBase implements IBREScript

{

    private static Logger logger = Logger.getLogger(SEL_BY_TRADEID_OTHER.class);

    /**
     * |宽带紧急开机时候，没有登记OTHER;
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 SEL_BY_TRADEID_OTHER() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeOther = databus.getDataset("TF_B_TRADE_OTHER");

        /* 开始逻辑规则校验 */
        if (listTradeOther.size() > 0)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 SEL_BY_TRADEID_OTHER() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
