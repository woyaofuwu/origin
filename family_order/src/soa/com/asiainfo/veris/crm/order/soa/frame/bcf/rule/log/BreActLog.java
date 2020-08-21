
package com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log;

import com.ailk.biz.util.LogUtil;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

public final class BreActLog extends LogBaseBean
{
    private static StringBuilder sb = new StringBuilder();

    /**
     * 获取当前时间
     * 
     * @return
     * @throws Exception
     */
    public final static long getStartTime() throws Exception
    {
        if (LogUtil.isEnable(LogBaseBean.LOG_TYPE_BRE_ACT))
        {
            return System.currentTimeMillis();
        }

        return -1;
    }

    /**
     * 发送规则日志
     * 
     * @param databus
     * @param subKey
     * @param lStartTime
     * @throws Exception
     */
    public final static void log(IData databus, String subKey, long lStartTime) throws Exception
    {
        // 是否记录日志
        if (lStartTime == -1)
        {
            return;
        }

        // 得到开销时间
        long lEndTime = System.currentTimeMillis();
        long lCostTime = lEndTime - lStartTime;

        // 是否超过监控值
        boolean bLog = LogUtil.isThresholdEnable(LogBaseBean.LOG_TYPE_BRE_ACT, lCostTime);

        if (!bLog)
        {
            return;
        }

        // 记录日志
        StringBuilder sbKey = new StringBuilder(50);

        sbKey.append(databus.getString("TRADE_TYPE_CODE", "")).append("_").append(subKey);

        sendLog(LOG_TYPE_BRE_ACT, sbKey.toString(), lStartTime, lCostTime, sb);
    }
}
