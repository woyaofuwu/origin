
package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ReOpenGPRSQry;

public class ReOpenGPRSSpecialAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1("ReopenGPRS");
        String[] reasonStr = getReasonForStopGPRS(mainTradeData.getUserId());
        for (int i = 0; i < reasonStr.length; i++)
        {
            if ("OVER15G".equals(reasonStr[i]))
            {
                // 设置 超出15G这个原因所对应的短信发送标识
                mainTradeData.setRsrvStr5("OVER15G_REOPEN");
            }
        }
    }

    /**
     * 取恢复前暂停GPRS的原因列表
     */
    public String[] getReasonForStopGPRS(String userId) throws Exception
    {
        String tradeTypeCode = "130";
        String acceptDate = SysDateMgr.getFirstDayOfThisMonth();
        String cancelTag = "0";
        IDataset dataset = ReOpenGPRSQry.getReasonForStopGPRS(tradeTypeCode, userId, acceptDate, cancelTag);

        int j = dataset.size();
        String[] rStr = new String[j];
        for (int i = 0; i < j; i++)
        {
            rStr[i] = dataset.getData(i).getString("RSRV_STR4", "");
        }

        return rStr;
    }
}
