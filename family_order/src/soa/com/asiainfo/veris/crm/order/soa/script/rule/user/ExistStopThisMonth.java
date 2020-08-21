
package com.asiainfo.veris.crm.order.soa.script.rule.user;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;

public class ExistStopThisMonth extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistStopThisMonth.class);

    /**
     * 判断用户是否当月停机
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistStopThisMonth() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strCurData = databus.getString(BreFactory.CUR_DATE);
        String strFirstDayOfThisMonth = databus.getString(BreFactory.FIRST_DAY_OF_THIS_MONTH);

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listUser = databus.getDataset("TF_F_USER");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listUser.iterator(); iter.hasNext();)
        {
            IData user = (IData) iter.next();

            String strLastStopTime = user.getString("LAST_STOP_TIME");

            if (strLastStopTime != null && !"".equals(strLastStopTime))
            {
                if (strLastStopTime.compareTo(strFirstDayOfThisMonth) > 0 && strLastStopTime.compareTo(strCurData) < 0)
                {
                    bResult = true;
                    break;
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistStopThisMonth() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
