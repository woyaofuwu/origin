
package com.asiainfo.veris.crm.order.soa.frame.bcf.syslog;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public final class SysLog
{

    private static final Logger logger = Logger.getLogger(SysLog.class);

    /**
     * 记录输入日志
     * 
     * @param svcName
     * @param head
     * @param inData
     * @throws Exception
     */
    public static void logDataIn(String svcName, IData head, IData inData) throws Exception
    {
        // 是否记录日志
        boolean isLog = BizEnv.getEnvBoolean("crm.svc.log", false);

        if (isLog == false)
        {
            return;
        }

        String sn = inData.getString("SERIAL_NUMBER", "sn");
        String staffId = CSBizBean.getVisit().getStaffId();

        if ((BizEnv.getEnvBoolean("crm.svc.log." + sn, false) == true) // 号码级
                || (BizEnv.getEnvBoolean("crm.svc.log." + staffId, false) == true) // 员工级
                || (BizEnv.getEnvBoolean("crm.svc.log." + svcName, false) == true) // 服务级
                || (BizEnv.getEnvBoolean("crm.svc.log.all", false) == true)) // 所有级

        {
            logger.info("CrmSvcLogIn[" + svcName + "]head=" + head.toString());
            logger.info("CrmSvcLogIn[" + svcName + "]inData=" + inData.toString());
        }
    }

    /**
     * 记录输出日志
     * 
     * @param svcName
     * @param inData
     * @param outDataset
     * @throws Exception
     */
    public static void logDataOut(String svcName, IData inData, IDataset outDataset) throws Exception
    {
        // 是否记录日志
        boolean isLog = BizEnv.getEnvBoolean("crm.svc.log", false);

        if (isLog == false)
        {
            return;
        }

        String sn = inData.getString("SERIAL_NUMBER", "sn");
        String staffId = CSBizBean.getVisit().getStaffId();

        if ((BizEnv.getEnvBoolean("crm.svc.log." + sn, false) == true) // 号码级
                || (BizEnv.getEnvBoolean("crm.svc.log." + staffId, false) == true) // 员工级
                || (BizEnv.getEnvBoolean("crm.svc.log." + svcName, false) == true) // 服务级
                || (BizEnv.getEnvBoolean("crm.svc.log.all", false) == true)) // 所有级
        {
            logger.info("CrmSvcLogOut[" + svcName + "]inData=" + inData.toString());
            logger.info("CrmSvcLogOut[" + svcName + "]outDataset=" + outDataset.toString());
        }
    }

}
