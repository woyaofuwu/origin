package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideuseractive.order.action.reg;

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

/**
 * 无手机宽带激活时候绑定优惠
 * @author lizj
 *
 */
public class NoPhoneBindDiscntAction implements ITradeAction{
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		 UcaData uca = btd.getRD().getUca();
         String UserId = uca.getUserId();
         String tradeId = btd.getMainTradeData().getRsrvStr1();
         IDataset otherTrades = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId,"FTTH");
         if (IDataUtil.isNotEmpty(otherTrades))
         {
        	 for(int i=0;i<otherTrades.size();i++){
        		 String modifyTag = otherTrades.getData(i).getString("MODIFY_TAG");
        		 String wideModeFee = otherTrades.getData(i).getString("RSRV_STR25");
        		  System.out.println("进入NoPhoneBindDiscntAction"+wideModeFee);
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
        	         newDiscnt.setRemark("无手机宽带激活时候绑定优惠");

        	         btd.add(uca.getSerialNumber(), newDiscnt);
        		 }
        	 }
         }
       
	}

}
