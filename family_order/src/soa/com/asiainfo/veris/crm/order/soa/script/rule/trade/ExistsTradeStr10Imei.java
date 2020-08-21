
package com.asiainfo.veris.crm.order.soa.script.rule.trade;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeStr10Imei extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeStr10Imei.class);

    /**
     * 判断输入的IMEI是否存在tf_F_user_imei表
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeStr10Imei() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取规则配置信息 */

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();

            IDataset listUserImei = databus.getDataset("TF_F_USER_IMEI");

            for (Iterator iterator = listUserImei.iterator(); iterator.hasNext();)
            {
                IData userImei = (IData) iterator.next();

                if (userImei.getString("IMEI").equals(trade.getString("RSRV_STR10")))
                {
                    bResult = true;
                    break;
                }
            }

            if (bResult)
            {
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeStr10Imei() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
