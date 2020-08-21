
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;

public class VoLTEChangeElementFilter implements IFilterOut
{

	@Override
	public IData transfterDataOut(IData input, BusiTradeData btd)
			throws Exception 
	{
		List<SvcTradeData> userSvcs = btd.getRD().getUca().getUserSvcBySvcId("190");
		
		String volte = "1";//0-非VoLTE用户 1-VoLTE用户
		if(userSvcs!=null && userSvcs.size()>0)
		{
			for (SvcTradeData userSvc : userSvcs)
 			{
				if(BofConst.MODIFY_TAG_DEL.equals(userSvc.getModifyTag()))
				{
					volte = "0";
					break;
				}
 			}
		}
		else
		{
			volte = "0";
		}
		input.put("VoLTE", volte);
		return input;
	}
	
}
