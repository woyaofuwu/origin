package com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.trade;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.hytuserchangetrade.order.requestdata.CreateHYTUserChangeRequestData;

public class HYTUserChangeTrade extends BaseTrade implements ITrade {

	/**
	 * 
	 */
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		CreateHYTUserChangeRequestData createHPersonUserRD = (CreateHYTUserChangeRequestData) btd.getRD();
		
		changeMainTradeData(btd,createHPersonUserRD);
		createDiscntTradeData(btd,createHPersonUserRD);// 处理用户优惠
	}

	
	/**
	 * @Description：修改主台账预留字段
	 * @param:@param btd
	 * @param:@param createHPersonUserRD
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-30下午03:46:09
	 */
	private void changeMainTradeData(BusiTradeData btd,CreateHYTUserChangeRequestData createHytUserChangeRD) throws Exception {
		UcaData uca = createHytUserChangeRD.getUca();
		btd.getMainTradeData().setRsrvStr1(createHytUserChangeRD.getShipId());//船只编号
		btd.getMainTradeData().setRsrvStr2(createHytUserChangeRD.getIsShipOwner());//是否船东
		btd.getMainTradeData().setRsrvStr3(createHytUserChangeRD.getIsShipOwnerDiscnt());//是否船东套餐
		btd.getMainTradeData().setRsrvStr4(SysDateMgr.getSysDateYYYYMMDDHHMMSS());//开始时间
		btd.getMainTradeData().setRsrvStr5(SysDateMgr.getAddMonthsLastDay(createHytUserChangeRD.getMonthOffset()));//结束时间
		btd.getMainTradeData().setRsrvStr6(createHytUserChangeRD.getDiscntCode());//优惠编码
		btd.getMainTradeData().setRsrvStr7(createHytUserChangeRD.getIsInterface());//是否是接口调用
	}
	public void createDiscntTradeData(BusiTradeData btd,CreateHYTUserChangeRequestData createHytUserChangeRD) throws Exception {
		MainTradeData mainTrade = btd.getMainTradeData();
		UcaData uca = createHytUserChangeRD.getUca();
		DiscntTradeData discntTradeData=new DiscntTradeData();
		discntTradeData.setUserId(uca.getUserId());
		discntTradeData.setUserIdA("-1");
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");
		discntTradeData.setElementId(createHytUserChangeRD.getDiscntCode());
		discntTradeData.setSpecTag("0");
		discntTradeData.setInstId( SeqMgr.getInstId());
		discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(createHytUserChangeRD.getMonthOffset()));
		
		discntTradeData.setModifyTag( BofConst.MODIFY_TAG_ADD);
		discntTradeData.setRsrvStr1(createHytUserChangeRD.getDiscntName());
		discntTradeData.setRemark("海洋通产品变更绑定套餐");
		btd.add(uca.getSerialNumber(), discntTradeData);
	}

}
