/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.agent;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent.AgentBankAcctInfoQry;

public class AgentPayStateBean extends CSBizBean {

	public IData modAgentPayInfoState(IData input) throws Exception {
		/*
		 * AgentPayStateBeanxxxxxxxxxxxxxxxx101
		 * {"page":"agent.AgentPayState","service"
		 * :"ajax","listener":"modAgentPayInfoState"
		 * ,"ROUTE_EPARCHY_CODE":"0898",
		 * "ROWID":"AAAdcRADRAAAJNOAAA,AAAdcRADRAAAJNOAAB" }
		 */

		IData resultData = new DataMap();
		//System.out.println("AgentPayStateBeanxxxxxxxxxxxxxxxx25 " + input);
		String[] rowid = input.getString("ROWID").split(",");
		String allremark = input.getString("ALL_REMARK","").trim();
		IData cond = new DataMap();
		
		for (int i = 0; i < rowid.length; i++) {
		   //System.out.println("AgentPayStateBeanxxxxxxxxxxxxxxxx31 " + rowid[i]);

			cond.clear();
			cond.put("X_ROWID", rowid[i].split("~")[0] );		
			cond.put("RSRV_STR5", "【"+CSBizBean.getVisit().getStaffId()+ "】将原状态"+rowid[i].split("~")[1]+"修改为8," + SysDateMgr.getSysTime());							
			cond.put("REMARK",  allremark);
			//System.out.println("AgentPayStateBeanxxxxxxxxxxxxxxxx36 " + cond);

			AgentBankAcctInfoQry.modAgentPayInfoState(cond);
		}

		resultData.put("RESULT_CODE", "0");

		return resultData;
	}

}
