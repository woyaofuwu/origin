
package com.asiainfo.veris.crm.order.soa.script.rule.fee;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistMobilePayLog extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistMobilePayLog.class);

    /**
     * 判断手机钱包帐户缴费
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistMobilePayLog() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRecvTime = ruleParam.getString(databus, "RECV_TIME");
        int iRecvFee = ruleParam.getInt(databus, "RECV_FEE");

        /* 获取业务台账，用户资料信息 */
        IDataset listMobilePaylog = databus.getDataset("TF_B_MOBILEPAYLOG");

        /* 开始逻辑规则校验 */
        int iCountMobilePatLog = listMobilePaylog.size();
        for (int iPaylog = 0; iPaylog < iCountMobilePatLog; iPaylog++)
        {
            if ("0".equals(listMobilePaylog.get(iPaylog, "CANCEL_TAG")) && ((String) listMobilePaylog.get(iPaylog, "RECV_TIME")).compareTo(strRecvTime) > 0 && (Integer) listMobilePaylog.get(iPaylog, "RECV_FEE") > iRecvFee)
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMobilePayLog() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMobilePayLog() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
