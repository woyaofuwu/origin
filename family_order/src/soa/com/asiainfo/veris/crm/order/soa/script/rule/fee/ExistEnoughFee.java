
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistEnoughFee extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistEnoughFee.class);

    /**
     * 判断某个费用项的钱是否缴足
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistEnoughFee() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strFeeTypeCode = ruleParam.getString(databus, "FEE_TYPE_CODE");
        int iFee = ruleParam.getInt(databus, "FEE");
        String strUserId = databus.getString("USER_ID");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeFeeSub = databus.getDataset("TF_B_TRADEFEE_SUB");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeFeeSub.iterator(); iter.hasNext();)
        {
            IData tradefeesub = (IData) iter.next();

            if (strFeeTypeCode.equals(tradefeesub.getString("FEE_TYPE_CODE")) && strUserId.equals(tradefeesub.getString("USER_ID")) && tradefeesub.getInt("FEE") >= iFee)
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistEnoughFee() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistEnoughFee() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
