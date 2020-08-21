
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsPriority extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsPriority.class);

    /**
     * 判断用户优先链接表
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsPriority() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strValidTag = ruleParam.getString(databus, "VALID_TAG");
        String strAuditTag = ruleParam.getString(databus, "AUDIT_TAG");

        /* 获取业务台账，用户资料信息 */
        String strUserId = databus.getString("USER_ID");
        String strSerialNumber = databus.getString("SERIAL_NUMBER");

        /* 开始逻辑规则校验 */
        IData param = new DataMap();
        param.put("USER_ID", strUserId);
        param.put("SERIAL_NUMBER", strSerialNumber);
        param.put("VALID_TAG", strValidTag);
        param.put("AUDIT_TAG", strAuditTag);

        bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsPriority", param);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsPriority() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
