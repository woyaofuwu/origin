
package com.asiainfo.veris.crm.order.soa.script.rule.payrelation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistAdvPayRelation extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistAdvPayRelation.class);

    /**
     * 存在高级付费关系
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistAdvPayRelation() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iCount = 0;
        String strSysdate = SysDateMgr.getSysDate("YYYYMMDD");

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listUserPayrelation = databus.getDataset("TF_A_PAYRELATION");

        /* 开始逻辑规则校验 */
        if (listUserPayrelation.size() < 2)
        {
            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistAdvPayRelation() " + bResult + "<<<<<<<<<<<<<<<<<<<");
            return false;
        }

        int iCountUserPayrelation = listUserPayrelation.size();
        for (int iPayRelation = 0; iPayRelation < iCountUserPayrelation; iPayRelation++)
        {
            if (((String) listUserPayrelation.get(iPayRelation, "END_CYCLE_ID")).compareTo(strSysdate) > 0)
            {
                iCount++;
            }

            if (iCount > 1)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistAdvPayRelation() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
