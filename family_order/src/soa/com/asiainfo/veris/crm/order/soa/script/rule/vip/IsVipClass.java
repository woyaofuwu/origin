
package com.asiainfo.veris.crm.order.soa.script.rule.vip;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class IsVipClass extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(IsVipClass.class);

    /**
     * 判断大客户类型
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 IsVipClass() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strClassId = ruleParam.getString(databus, "CLASS_ID");

        /* 获取业务台账，用户资料信息 */
        IDataset listCustVip = databus.getDataset("TF_F_CUST_VIP");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listCustVip.iterator(); iter.hasNext();)
        {
            IData vip = (IData) iter.next();

            if ("*".equals(strClassId) || strClassId.equals(vip.getString("VIP_CLASS_ID")))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 IsVipClass() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
