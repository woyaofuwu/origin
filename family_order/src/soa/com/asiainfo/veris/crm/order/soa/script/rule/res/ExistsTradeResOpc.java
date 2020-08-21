
package com.asiainfo.veris.crm.order.soa.script.rule.res;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;

public class ExistsTradeResOpc extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(ExistsTradeResOpc.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsTradeResOpc() >>>>>>>>>>>>>>>>>>");
        boolean bResult = false;
        IDataset listTradeRes = databus.getDataset("TF_B_TRADE_RES");
        if (IDataUtil.isNotEmpty(listTradeRes))
        {
            int size = listTradeRes.size();
            for (int i = 0; i < size; i++)
            {
                IData tradeRes = listTradeRes.getData(i);
                if ("1".equals(tradeRes.getString("RES_TYPE_CODE")) && "0".equals(tradeRes.getString("MODIFY_TAG")) && !"".equals(tradeRes.getString("RSRV_STR3")))
                {
                    // 表示SIM卡有OPC值
                    bResult = true;
                    break;
                }
            }
        }
        if (!bResult)
        {
            IData param = new DataMap();
            param.put("USER_ID", databus.getString("USER_ID"));
            bResult = Dao.qryByRecordCount("TD_S_CPARAM", "ExistsOpcValue", param);
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsTradeResOpc() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        return bResult;
    }

}
