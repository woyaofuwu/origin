package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class DestroyGroupAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String sNumber = btd.getRD().getUca().getSerialNumber();
		
		IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(sNumber,"61");
		if (IDataUtil.isNotEmpty(snRelationSet)) {
			String roleCodeB = snRelationSet.getData(0).getString("ROLE_CODE_B");
			
			if ("2".equals(roleCodeB)) 
			{
				IData param=new DataMap();
				IDataset memberList = new DatasetList();
				IData memberData = new DataMap();
				memberData.put("tag", "1");
				memberData.put("SERIAL_NUMBER_B", sNumber);
				memberList.add(memberData);
				
				param.put("MEB_LIST", memberList.toString());
				param.put("SERIAL_NUMBER", sNumber);
				param.put("CANCELL_MEB", "C1");
				IDataset results=CSAppCall.call("SS.DelGroupNetMemberRegSVC.tradeReg", param);
			}
			
			/*if ("1".equals(roleCodeB)) 
			{
				IData param = new DataMap();
				param.put("SERIAL_NUMBER", sNumber);
				IDataset results=CSAppCall.call("SS.DestroyGroupRegSVC.tradeReg", param);
				return;
			}else if ("2".equals(roleCodeB))
			{
				IData param=new DataMap();
				IDataset memberList = new DatasetList();
				IData memberData = new DataMap();
				memberData.put("tag", "1");
				memberData.put("SERIAL_NUMBER_B", sNumber);
				memberList.add(memberData);
				
				param.put("MEB_LIST", memberList.toString());
				param.put("SERIAL_NUMBER", sNumber);
				param.put("CANCELL_MEB", "C1");
				IDataset results=CSAppCall.call("SS.DelGroupNetMemberRegSVC.tradeReg", param);
				return;
			}*/
		}
		
	}

}
