package com.asiainfo.veris.crm.iorder.soa.family.common.action;

import java.util.List;

import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.FamilyInternetTvOpenRequestData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;

/**
 * 家庭魔百和新开：OTHER表添加familyMemberInstId标识
 * @author zhangxi
 *
 */
public class DealFamilyTvOpen4800Action implements ITradeAction {

	@Override
	public void executeAction(@SuppressWarnings("rawtypes") BusiTradeData btd) throws Exception {

		FamilyInternetTvOpenRequestData reqData = (FamilyInternetTvOpenRequestData)btd.getRD();

		@SuppressWarnings("unchecked")
		List<OtherTradeData> otds = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);

		if(otds != null && otds.size()>0){

			for(OtherTradeData otd : otds){

				if("TOPSETBOX".equals(otd.getRsrvValueCode())){

					otd.setRsrvStr23(reqData.getFamilyMemberInstId());

				}else{

					continue;

				}
			}

		}

	}

}
