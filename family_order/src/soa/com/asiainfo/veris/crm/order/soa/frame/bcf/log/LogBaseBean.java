
package com.asiainfo.veris.crm.order.soa.frame.bcf.log;

import com.ailk.biz.util.LogUtil;

public class LogBaseBean
{
    public final static String LOG_PATH = "c:\\log\\";// 调试日志目录 

    // crm 2000 - 6000
    public final static String LOG_TYPE_TRADE_ACTION = "2015"; // 业务受理(动作)

    public final static String LOG_TYPE_CUST_TOUCH = "2018"; // 客户接触

    public final static String LOG_TYPE_CUST_TOUCH_TRACE = "2019"; // 客户接触轨迹

    public final static String LOG_TYPE_OPERLOG = "2020"; // TF_F_OPERLOG 日志(IVR)

    public final static String LOG_TYPE_STAFFOPER = "2021"; // TF_B_STAFFOPERLOG 日志

    public final static String LOG_TYPE_CRMOPER = "2022"; // TL_B_CRM_OPERLOG 日志

    public final static String LOG_TYPE_BRE_ACT = "2000"; // 业务规则(脚本)

    public final static String LOG_TYPE_BRE_BIZ = "2001"; // 业务规则(业务)

    public final static String
            LOG_TYPE_AUDITOPER = "2028"; // 审计

    public final static String LOG_SEPARATOR_COMMA = "|";

    public final static String LOG_SEPARATOR_COMMA_0 = ":";

    public final static String LOG_SEPARATOR_COMMA_1 = "#";

    public final static String LOG_SEPARATOR_COMMA_2 = "~~";




    /***
     * 根据日志类型发送日志信息
     * 
     * @param msgType
     * @param key
     * @param startTime
     * @param nowCost
     * @param msg
     * @throws Exception
     */
    public final static void sendLog(String msgType, String key, long startTime, long nowCost, StringBuilder msg) throws Exception
    {
        LogUtil.sendLog(msgType, key, startTime, nowCost, msg.toString());
    }
}
