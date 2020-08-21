
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.log;

import com.ailk.biz.util.LogUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.log.LogBaseBean;

public final class ActionLog extends LogBaseBean
{
    private static StringBuilder sb = new StringBuilder();

    public final static long getStartTime() throws Exception
    {
        if (LogUtil.isEnable(LogBaseBean.LOG_TYPE_TRADE_ACTION))
        {
            return System.currentTimeMillis();
        }

        return -1;
    }

    public final static void log(String tradeTypeCode, String clazz, long lStartTime) throws Exception
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
        boolean bLog = LogUtil.isThresholdEnable(LogBaseBean.LOG_TYPE_TRADE_ACTION, lCostTime);

        if (bLog == false)
        {
            return;
        }

        // 当前业务类型
        String strKey = tradeTypeCode + "_" + clazz.substring(6);

        sendLog(LOG_TYPE_TRADE_ACTION, strKey, lStartTime, lCostTime, sb);
    }
}
