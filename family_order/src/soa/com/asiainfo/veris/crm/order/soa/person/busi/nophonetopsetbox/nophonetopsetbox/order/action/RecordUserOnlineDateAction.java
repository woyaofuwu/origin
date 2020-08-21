package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class RecordUserOnlineDateAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String userId=mainTrade.getString("USER_ID","");
		String tradeStaffId=mainTrade.getString("TRADE_STAFF_ID","");
		String tradeDepartId=mainTrade.getString("TRADE_DEPART_ID","");
		
		IData param=new DataMap();
		param.put("USER_ID", userId);
		param.put("IN_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		param.put("IS_BACK", "0");		//0是表示押金没有被返还，1表示已经被返还
		param.put("UPDATE_STAFF_ID", tradeStaffId);
		param.put("UPDATE_DEPART_ID", tradeDepartId);
		
		UserResInfoQry.insertTopsetboxOnline(param);
		
		/*
		 * 用户完工将用户进行撤单的数据删除
		 */
		UserResInfoQry.delRollbackTopSetBox(userId);
		
	}
	
}
