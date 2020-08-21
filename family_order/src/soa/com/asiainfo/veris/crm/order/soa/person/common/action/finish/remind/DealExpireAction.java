
package com.asiainfo.veris.crm.order.soa.person.common.action.finish.remind;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class DealExpireAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        IData param = new DataMap();
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String execTime = mainTrade.getString("EXEC_TIME");
        param.put("DEAL_ID", SeqMgr.getTradeId());
        param.put("USER_ID", userId);
        param.put("PARTITION_ID", userId.substring(userId.length() - 4));

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_STATE", "0");
        if ("603".equals(tradeTypeCode) || "617".equals(tradeTypeCode) || "618".equals(tradeTypeCode) || "632".equals(tradeTypeCode) || "7221".equals(tradeTypeCode) || "7222".equals(tradeTypeCode) || "7223".equals(tradeTypeCode)
                || "7224".equals(tradeTypeCode))
        {
            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_WIDENETSTOP);
            param.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth());
        }
        else
        {
            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_WIDENETPRODUCT);
            param.put("EXEC_TIME", SysDateMgr.getFirstDayOfNextMonth());
        }
        param.put("EXEC_MONTH", param.getString("EXEC_TIME").substring(5, 7));
        param.put("TRADE_ID", tradeId);
        Dao.insert("TF_F_EXPIRE_DEAL", param);

    }

}
