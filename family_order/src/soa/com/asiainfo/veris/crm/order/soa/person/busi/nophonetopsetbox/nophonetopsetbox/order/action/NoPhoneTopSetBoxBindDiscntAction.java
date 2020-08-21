package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;

public class NoPhoneTopSetBoxBindDiscntAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		 UcaData uca = btd.getRD().getUca();
         String UserId = uca.getUserId();
         String tradeId = btd.getMainTradeData().getRsrvStr10();
         //如果是无手机魔百和换机直接返回
         if (StringUtils.isBlank(tradeId)){
         	return;
		 }

         String KD_SerialNum = btd.getMainTradeData().getRsrvStr3();
        // IDataset wideNetTrades = TradeWideNetInfoQry.queryTradeWideNet(tradeId);
         IDataset topSetBoxTrades = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId,"TOPSETBOX");
         System.out.println("进入NoPhoneTopSetBoxBindDiscntAction"+KD_SerialNum);
         if (IDataUtil.isNotEmpty(topSetBoxTrades))
         {
        	 for(int i=0;i<topSetBoxTrades.size();i++){
        		 String modifyTag = topSetBoxTrades.getData(i).getString("MODIFY_TAG");
        		 String wideModeFee = topSetBoxTrades.getData(i).getString("RSRV_STR25");

        		 if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)&&StringUtils.isNotEmpty(wideModeFee)){
        			 if(wideModeFee.contains(",")){
        				 wideModeFee=wideModeFee.split(",")[0];
        			 }
        			 DiscntTradeData newDiscnt = new DiscntTradeData();
        	         newDiscnt.setUserId(UserId);
        	         newDiscnt.setProductId("-1");
        	         newDiscnt.setPackageId("-1");
        	         newDiscnt.setElementId(wideModeFee);
        	         newDiscnt.setInstId(SeqMgr.getInstId());
        	         newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        	         newDiscnt.setStartDate(SysDateMgr.getSysTime());
        	         newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(1));
        	         newDiscnt.setRemark("无手机魔百和办理时候绑定优惠");

        	         btd.add(uca.getSerialNumber(), newDiscnt);
        		 }
        	 }
         }
		
	}

}
