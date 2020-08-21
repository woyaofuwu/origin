package com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.createhytusertrade.order.requestdata.CreateHYTPersonUserRequestData;

public class CreateHYTPersonUserTrade extends BaseTrade implements ITrade {

	/**
	 * 
	 */
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		CreateHYTPersonUserRequestData createHYTPersonUserRD = (CreateHYTPersonUserRequestData) btd.getRD();
		
		changeMainTradeData(btd,createHYTPersonUserRD);
		createOtherTradeData(btd,createHYTPersonUserRD);// 处理用户台账
		if(StringUtils.isNotBlank(createHYTPersonUserRD.getDiscntCode())){
			createDiscntTradeData(btd,createHYTPersonUserRD);// 处理用户优惠
			
		}
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
	private void changeMainTradeData(BusiTradeData btd,CreateHYTPersonUserRequestData data) throws Exception {
		UcaData uca = data.getUca();
		btd.getMainTradeData().setRsrvStr1(data.getShipId());//船只编号
		btd.getMainTradeData().setRsrvStr2(data.getIsShipOwner());//是否船东
		btd.getMainTradeData().setRsrvStr3(data.getIsShipOwnerDiscnt());//是否船东套餐
		btd.getMainTradeData().setRsrvStr4(SysDateMgr.getSysDateYYYYMMDDHHMMSS());//开始时间
		btd.getMainTradeData().setRsrvStr5(SysDateMgr.getAddMonthsLastDay(data.getMonthOffset()));//结束时间
		btd.getMainTradeData().setRsrvStr6(data.getDiscntCode());//优惠编码
		btd.getMainTradeData().setRsrvStr7(uca.getCustomer().getPsptId());//身份证号码
		
	}


	public void createOtherTradeData(BusiTradeData btd,CreateHYTPersonUserRequestData data) throws Exception {
		
		UcaData uca = data.getUca();
		String instId = SeqMgr.getInstId();
		OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("HYT");
        otherTradeData.setRsrvValue("HYT_DATA");
        otherTradeData.setUserId(uca.getUserId());
        otherTradeData.setInstId(instId);
        btd.getMainTradeData().setRsrvStr7(instId);//优惠编码
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark("办理海洋通套餐"); 
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTradeData.setRsrvStr1(data.getShipId());//船只编号
        otherTradeData.setRsrvStr2(data.getIsShipOwner());//是否船东
        otherTradeData.setRsrvStr3(data.getIsShipOwnerDiscnt());//是否船东套餐
        otherTradeData.setRsrvStr4(data.getDiscntCode());//优惠编码
        otherTradeData.setRsrvStr5("1");//是否开通
        btd.add(uca.getSerialNumber(), otherTradeData);
	}
	public void createDiscntTradeData(BusiTradeData btd,CreateHYTPersonUserRequestData data) throws Exception {
		MainTradeData mainTrade = btd.getMainTradeData();
		UcaData uca = data.getUca();
		DiscntTradeData discntTradeData=new DiscntTradeData();
		discntTradeData.setUserId(uca.getUserId());
		discntTradeData.setUserIdA("-1");
		discntTradeData.setPackageId("-1");
		discntTradeData.setProductId("-1");
		discntTradeData.setElementId(data.getDiscntCode());
		discntTradeData.setSpecTag("0");
		discntTradeData.setInstId( SeqMgr.getInstId());
		discntTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		discntTradeData.setEndDate(SysDateMgr.getAddMonthsLastDay(data.getMonthOffset()));
		discntTradeData.setRsrvStr1(data.getDiscntName());
		discntTradeData.setModifyTag( BofConst.MODIFY_TAG_ADD);

		discntTradeData.setRemark("海洋通开户绑定套餐");
		btd.add(uca.getSerialNumber(), discntTradeData);
	}

}
