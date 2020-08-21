
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistUserOthers extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistUserOthers.class);

    /**
     * 模糊判断用户other 的 rsrv_value_code 数据
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistUserOthers() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
        {
            IData userOther = (IData) iter.next();

            if (userOther.getString("RSRV_VALUE_CODE").indexOf(strRsrvValueCode) > -1)
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistUserOthers() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
