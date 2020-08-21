
package com.asiainfo.veris.crm.order.soa.script.rule.cancel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

public class TBCCheckFee extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBCCheckFee.class);

    /**
     * 判断开户后是否产生过费用
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCCheckFee() >>>>>>>>>>>>>>>>>>");

        IData hisTradeData = databus.getData("TRADE_INFO");// 历史订单信息
        /* 自定义区域 */
        boolean bResult = false;
        if ("10".equals(databus.getString("TRADE_TYPE_CODE")))
        {

            IData param = new DataMap();
            param.put("USER_ID", databus.getString("USER_ID"));
            param.put("TRADE_ID", databus.getString("TRADE_ID"));
            param.put("TRADE_TYPE_CODE", databus.getInt("TRADE_TYPE_CODE"));
            param.put("ACCEPT_MONTH", hisTradeData.getInt("ACCEPT_MONTH"));
            param.put("TRADE_TIME", hisTradeData.getString("ACCEPT_DATE"));
            param.put("X_CHOICE_TAG", "0");

            // QAM_ISCANCANCEL
            IDataset list = AcctInfoQry.qamIsCanCancel(param);
            // QAM_CRM_CheckCanCancel
            if (list.size() > 0)
            {
                if (list.getData(0).getInt("RECORD_COUNT") > 0)
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751151, "业务取消校验[业务费用校验]：请先返销办理此业务后缴纳的费用！");
                    return true;
                }
            }
            else
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751151, "业务取消校验[业务费用校验]：获取业务后是否有缴费数据失败！");
                return true;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBCCheckFee() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
