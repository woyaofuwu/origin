package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;

public class CPESvcStateDealAction implements ITradeAction {
	
	/**
     * Copyright: Copyright 2016 Asiainfo
     * 
     * @Description: CPE办理流量加油包时，如果是流量封顶状态，则恢复CPE上网 【CPESvcStateDealAction】
     * @author: songlm
     * 
     */

	public void executeAction(BusiTradeData btd) throws Exception 
	{
		List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
		String userBrand = btd.getRD().getUca().getBrandCode();//获取品牌

		//循环优惠子台帐
		for (DiscntTradeData discntTradeData : discntTradeDatas) {

			//如果是新增优惠，且优惠是CPE的流量加油包
			if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()) && "6483".equals(discntTradeData.getDiscntCode()))
			{
				if ("CPE1".equals(userBrand))
				{
					SvcStateTradeData svcStateData = btd.getRD().getUca().getUserSvcsStateByServiceId("220");
					if (svcStateData != null) 
					{
						String stateCode = svcStateData.getStateCode();
						if ("D".equals(stateCode)) 
						{
							SvcStateTradeData svcStateData1 = svcStateData.clone();
							SvcStateTradeData svcStateData2 = svcStateData.clone();

							svcStateData1.setStateCode("D");
							svcStateData1.setEndDate(SysDateMgr.getSysTime());
							svcStateData1.setModifyTag(BofConst.MODIFY_TAG_DEL);
							svcStateData1.setRemark("110产品变更，办理流量加油包--截止旧服务状态(D流量封顶)");

							svcStateData2.setStateCode("0");
							svcStateData2.setInstId(SeqMgr.getInstId());
							svcStateData2.setStartDate(SysDateMgr.getSysTime());
							svcStateData2.setEndDate(SysDateMgr.END_DATE_FOREVER);
							svcStateData2.setModifyTag(BofConst.MODIFY_TAG_ADD);
							svcStateData2.setRemark("110产品变更，办理流量加油包--新增新服务状态(0恢复开通上网)");

							btd.add(btd.getRD().getUca().getSerialNumber(),svcStateData1);
							btd.add(btd.getRD().getUca().getSerialNumber(),svcStateData2);
							
							//将新状态记录到tf_f_user的user_state_codeset
							UserTradeData userData = btd.getRD().getUca().getUser().clone();
							userData.setModifyTag(BofConst.MODIFY_TAG_UPD);
							userData.setUserStateCodeset("0");
							btd.add(btd.getRD().getUca().getSerialNumber(), userData);//加入busiTradeData中
	    		            
							break;
						}
					}
				}
				else 
				{
					CSAppException.appError("20150824", "只有CPE用户才能办理！");
				}
			}
		}
	}
}
