
package com.asiainfo.veris.crm.order.soa.frame.bre.script.data.common;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;

public class TimeUtil extends BreBase implements IBREDataPrepare
{

    private static final Logger logger = Logger.getLogger(BreBase.class);

    public void run(IData databus) throws Exception
    {
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TimeUtil() >>>>>>>>>>>>>>>>>>");
            logger.debug(" FIRST_DAY_OF_NEXT_MONTH = [" + SysDateMgr.getFirstDayOfNextMonth() + "]");
            logger.debug(" FIRST_DAY_OF_THIS_MONTH = [" + SysDateMgr.getFirstDayOfThisMonth() + "]");
            logger.debug(" LAST_DAY_OF_CUR_MONTH = [" + SysDateMgr.getLastDateThisMonth() + "]");
            logger.debug(" SYSDATE = [" + SysDateMgr.getSysDate() + "]");
        }

        /* 下月第一天 */
        databus.put("FIRST_DAY_OF_NEXT_MONTH", SysDateMgr.getFirstDayOfNextMonth());

        /* 本月第一天 */
        databus.put("FIRST_DAY_OF_THIS_MONTH", SysDateMgr.getFirstDayOfThisMonth());

        /* 本月最后一天 */
        databus.put("LAST_DAY_OF_CUR_MONTH", SysDateMgr.getLastDateThisMonth());

        /* 当前系统时间 */
        databus.put("CUR_DATE", SysDateMgr.getSysDate());
        databus.put("SYSDATE", SysDateMgr.getSysDate());

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TimeUtil() <<<<<<<<<<<<<<<<<<<");
        }
    }

}
