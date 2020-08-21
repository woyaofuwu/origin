
package com.asiainfo.veris.crm.order.soa.script.rule.product;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class ExistsTradeDepositEnMode extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(ExistsTradeDepositEnMode.class);

    /**
     * 是否办理了哪个预存奖励业务大类
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeDepositEnMode() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strSysdate = databus.getString("SYSDATE");

        /* 获取规则配置信息 */
        String strRsrvStr1 = ruleParam.getString(databus, "RSRV_STR1");

        /* 获取业务台账，用户资料信息 */
        IDataset listTrade = databus.getDataset("TF_B_TRADE");

        /* 开始逻辑规则校验 */
        for (Iterator iter = listTrade.iterator(); iter.hasNext();)
        {
            IData trade = (IData) iter.next();

            IData product = BreQryForProduct.getProductByIdOfAll(trade.getString("RSRV_STR1"));

            if (strRsrvStr1.equals(product.getString("RSRV_STR1")) && strSysdate.compareTo(product.getString("START_DATE")) > 0 && strSysdate.compareTo(product.getString("END_DATE")) < 0)
            {
                bResult = true;
                break;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeDepositEnMode() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
