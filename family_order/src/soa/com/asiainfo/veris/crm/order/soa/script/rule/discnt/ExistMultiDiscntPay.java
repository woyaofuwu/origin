
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistMultiDiscntPay extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistMultiDiscntPay.class);

    /**
     * 判断用户的所有合账用户是否存在commpara.param_attr = 2001 and param_code = rule_data.getString("PARAM_CODE") 的数据
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistMultiDiscntPay() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strEparchyCode = ruleParam.getString(databus, "EPARCHY_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserDiscnt = databus.getDataset("TF_F_USER_DISCNT_NORPAY_NOSELF");

        /* 查询区域 */
        IDataset listCommpara = BreQryForCommparaOrTag.getCommparaCode1("CSM", 2001, strParamCode, strEparchyCode);

        /* 开始逻辑规则校验 */
        int iCountUserDiscnt = listUserDiscnt.size();
        int iCountCommpara = listCommpara.size();
        for (int iUserDiscnt = 0; iUserDiscnt < iCountUserDiscnt; iUserDiscnt++)
        {
            String strDiscntCode = (String) listUserDiscnt.get(iUserDiscnt, "DISCNT_CODE");

            for (int iCommpara = 0; iCommpara < iCountCommpara; iCommpara++)
            {
                if (strDiscntCode.equals(listCommpara.get(iCommpara, "PARA_CODE1")))
                {
                    if (logger.isDebugEnabled())
                        logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMultiDiscntPay() " + true + "<<<<<<<<<<<<<<<<<<<");
                    return true;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistMultiDiscntPay() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
