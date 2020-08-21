package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;

public class StopOpenAPNTagAction implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String user_id = btd.getRD().getUca().getUserId();
		String mainSn = btd.getRD().getUca().getSerialNumber();
		IDataset otherData = queryUserOtherInfosByIdRsrvCode(null, user_id, "USER_APNTAG");
		if(IDataUtil.isNotEmpty(otherData)&&mainSn.equals(otherData.getData(0).getString("RSRV_VALUE"))){
			IData otherDataSub = otherData.getData(0);
			OtherTradeData tradeData= new OtherTradeData(otherDataSub);
			tradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
			tradeData.setIsNeedPf(BofConst.IS_NEED_PF_YES);
			btd.add(mainSn,tradeData);
		}
	}
	/**
	 * 获取TF_F_USER_OTHER表数据
	 * 
	 * @param tradeId
	 * @param userId
	 * @param rsrvCode
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserOtherInfosByIdRsrvCode(String tradeId, String userId, String rsrvCode) throws Exception
	{
		IData data = new DataMap();
		data.put("USER_ID", userId);
		data.put("RSRV_VALUE_CODE", rsrvCode);
		IDataset userOtherInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHERINFO_BY_USERID_RSRVCODE", data);
		if(IDataUtil.isNotEmpty(userOtherInfos)){
			return userOtherInfos;
		}
		return null;
	}
}
