
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsOtherRsrv30 extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsOtherRsrv30.class);

    /**
     * 判断客户名称是否包涵特殊字符
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsOtherRsrv30() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */
        String strModifyTag = ruleParam.getString(databus, "MODIFY_TAG");
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");

        /* 获取业务台账，用户资料信息 */
        IDataset listTradeOther = databus.getDataset("TF_B_TRADE_OTHER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTradeOther.iterator(); iter.hasNext();)
        {
            IData other = (IData) iter.next();

            if (strRsrvValueCode.equals(other.getString("RSRV_VALUE_CODE")) && ("*".equals(strModifyTag) || strModifyTag.equals(other.getString("MODIFY_TAG")))
                    && ("".equals(other.getString("RSRV_STR30", "")) || "0".equals(other.getString("RSRV_STR30", ""))))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsOtherRsrv30() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
