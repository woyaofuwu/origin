
package com.asiainfo.veris.crm.order.soa.script.rule.other;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsJoinGGcardNum extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsJoinGGcardNum.class);

    /**
     * 判断用户other表符合的数据记录条数
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistCustName() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int iCount = 0;

        /* 获取规则配置信息 */
        String strRsrvValueCode = ruleParam.getString(databus, "RSRV_VALUE_CODE");
        String strUserId = ruleParam.getString(databus, "USER_ID");
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");
        String strRsrvStr2 = ruleParam.getString(databus, "RSRV_STR2");
        int iNum = ruleParam.getInt(databus, "NUM");

        /* 获取业务台账，用户资料信息 */
        IDataset listUserOther = databus.getDataset("TF_F_USER_OTHER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listUserOther.iterator(); iter.hasNext();)
        {
            IData other = (IData) iter.next();

            if (strRsrvValueCode.equals(other.getString("RSRV_VALUE_CODE")) && ("*".equals(strRsrvStr1) || strRsrvStr1.equals(other.getString("RSRV_STR1"))) && ("*".equals(strRsrvStr2) || strRsrvStr2.equals(other.getString("RSRV_STR2"))))
            {
                iCount++;
            }
        }

        if (iCount == iNum)
        {
            bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistCustName() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
