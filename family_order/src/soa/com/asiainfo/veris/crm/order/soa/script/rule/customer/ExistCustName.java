
package com.asiainfo.veris.crm.order.soa.script.rule.customer;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistCustName extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistCustName.class);

    /**
     * 判断客户名称是否包涵特殊字符
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistCustName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strCustName = ruleParam.getString(databus, "CUST_NAME");

        /* 获取业务台账，用户资料信息 */
        IDataset listCustomer = databus.getDataset("TF_F_CUSTOMER");

        /* 开始逻辑规则校验 */
        bResult = ((String) listCustomer.get(0, "CUST_NAME")).indexOf(strCustName) > -1;

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistCustName() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
