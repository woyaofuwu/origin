
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

/**
 * 免费换卡完工插TF_F_USER_OTHERSERV 表
 * 
 * @author
 */
public class FreeChangeCardFinishAction implements ITradeFinishAction
{

	protected static Logger log = Logger.getLogger(FreeChangeCardFinishAction.class);
	
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");

        try 
        {
        	IDataset tradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId);
            insertOtherServTable(tradeInfos);
		} 
        catch (Exception e) 
        {
        	//log.info("(e);
		}
    }

    public void insertOtherServTable(IDataset otherSet) throws Exception
    {
        for (int i = 0; i < otherSet.size(); i++)
        {
            IData param = new DataMap();
            String userId = otherSet.getData(i).getString("USER_ID", "");
            param.put("USER_ID", userId);
            long partId = Long.valueOf(userId) % 10000;
            param.put("PARTITION_ID", String.valueOf(partId));
            param.put("SERVICE_MODE", otherSet.getData(i).getString("RSRV_VALUE_CODE", ""));
            param.put("SERIAL_NUMBER", otherSet.getData(i).getString("RSRV_STR3", ""));
            param.put("PROCESS_INFO", otherSet.getData(i).getString("RSRV_VALUE", ""));
            param.put("RSRV_STR4", otherSet.getData(i).getString("RSRV_STR4", ""));
            param.put("RSRV_STR5", otherSet.getData(i).getString("RSRV_STR5", ""));
            param.put("RSRV_STR6", otherSet.getData(i).getString("RSRV_STR6", ""));
            param.put("PROCESS_TAG", otherSet.getData(i).getString("PROCESS_TAG", ""));
            param.put("STAFF_ID", otherSet.getData(i).getString("STAFF_ID", ""));
            param.put("DEPART_ID", otherSet.getData(i).getString("DEPART_ID", ""));
            param.put("START_DATE", otherSet.getData(i).getString("START_DATE", ""));
            param.put("END_DATE", otherSet.getData(i).getString("END_DATE", ""));
            param.put("INST_ID", otherSet.getData(i).getString("INST_ID"));
            Dao.insert("TF_F_USER_OTHERSERV", param);
        }

    }
}
