
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistMultiPayRelationYCYX extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistMultiPayRelationYCYX.class);

    /**
     * 判断用户的所有合账用户是否存在 ruleParam.getString(databus, "DISCNT_CODE") 配置的优惠
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistMultiPayRelationYCYX() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strDiscntCode = ruleParam.getString(databus, "DISCNT_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT_NORPAY_NOSELF");

        /* 开始逻辑规则校验 */
        int iCountUserDiscnt = listUserDiscnt.size();
        for (int iUserDiscnt = 0; iUserDiscnt < iCountUserDiscnt; iUserDiscnt++)
        {
            if (strDiscntCode.equals(listUserDiscnt.get(iUserDiscnt, "DISCNT_CODE")))
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMultiPayRelationYCYX() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMultiPayRelationYCYX() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
