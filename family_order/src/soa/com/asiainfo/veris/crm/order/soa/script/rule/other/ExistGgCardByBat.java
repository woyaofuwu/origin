
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistGgCardByBat extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistGgCardByBat.class);

    /**
     * 刮刮卡资料查询,通过rsrv_str6字段判断
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistGgCardByBat() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRsrvStr6 = ruleParam.getString(databus, "RSRV_STR6");
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");

        /* 开始逻辑规则校验 */
        int iCountUserOther = listUserOther.size();
        for (int iUserOther = 0; iUserOther < iCountUserOther; iUserOther++)
        {
            if (strRsrvValueCode.equals(listUserOther.get(iUserOther, "RSRV_VALUE_CODE")) && ("".equals(strRsrvStr6)) && strRsrvStr6.equals(listUserOther.get(iUserOther, "RSRV_STR6")))
            {
                if (logger.isDebugEnabled())
                    logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistGgCardByBat() " + true + "<<<<<<<<<<<<<<<<<<<");
                return true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistGgCardByBat() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
