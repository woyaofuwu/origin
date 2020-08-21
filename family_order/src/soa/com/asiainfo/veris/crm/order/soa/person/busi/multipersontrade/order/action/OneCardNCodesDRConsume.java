package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class OneCardNCodesDRConsume implements ITradeAction
{
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String sn = btd.getRD().getUca().getSerialNumber();
		
		IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(sn,"61");
		String  roleCodeB=snRelationSet.getData(0).getString("ROLE_CODE_B");
		if (IDataUtil.isNotEmpty(snRelationSet) && "2".equals(roleCodeB)) 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：多人约消用户不允许办理一卡双号");
			return;
		}
		
	}
}
