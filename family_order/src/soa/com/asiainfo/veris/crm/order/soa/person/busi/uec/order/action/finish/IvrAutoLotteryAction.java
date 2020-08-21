package com.asiainfo.veris.crm.order.soa.person.busi.uec.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

public class IvrAutoLotteryAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String execTime = mainTrade.getString("ACCEPT_DATE");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String inModeCode = mainTrade.getString("IN_MODE_CODE");
        String tradeStaffId = mainTrade.getString("TRADE_STAFF_ID");
        
        //非IVR渠道的业务直接返回
        if(!"1".equals(inModeCode) || !"ITFCC000".equals(tradeStaffId)){
        	return;
        }

        IData param = new DataMap();

        param.put("DEAL_ID", SeqMgr.getTradeId());
        param.put("USER_ID", userId);
        param.put("PARTITION_ID", userId.substring(userId.length() - 4));
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("IN_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_STATE", "0");
        param.put("DEAL_TYPE", "AutoLottery");
        param.put("EXEC_TIME", execTime);
        param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
        param.put("TRADE_ID", tradeId);

        Dao.insert("TF_F_EXPIRE_DEAL", param);

	}

}
