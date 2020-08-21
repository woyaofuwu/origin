package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.action;

import java.util.Date;
import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class OneCardMultiNoHandleTagAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String userId = btd.getMainTradeData().getUserId();
		
		List<RelationTradeData> tradeRels = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);
		if(tradeRels != null && tradeRels.size() >0 )
		{
			for(RelationTradeData tradeRela:tradeRels)
			{
				String tag = tradeRela.getModifyTag();
				if(BofConst.MODIFY_TAG_ADD.equals(tag))
				{
					//REQ201907110021 办和多号送话费活动需求 --新增对应虚拟号而给主号办理营销活动标识
					 IDataset relaUUInfo = RelaUUInfoQry.qryAllRelaUUByUidA(userId,"M2",Route.CONN_CRM_CG);
		             if(IDataUtil.isNotEmpty(relaUUInfo)){
		            	boolean flg = true;
		             	for(int i=0;i<relaUUInfo.size();i++){
		             		String startDate = relaUUInfo.getData(i).getString("START_DATE");
		             		String end = SysDateMgr.addMonths(startDate,6);
		    	        	Date now = new Date();
		    	        	String sysdate = SysDateMgr.date2String(now, SysDateMgr.PATTERN_STAND_YYYYMMDD);
		    	        	if(sysdate.compareTo(end)<=0){
		    	        		flg = false;
		    	        		break;
		    	        	}
		    	        	
		             	}
		             	if(flg){
		             		tradeRela.setRsrvTag3("Y");
		             	}
		             }else{
		            	 tradeRela.setRsrvTag3("Y"); 
		             }
					
				}
				
			}
				
			
		}
		
	}

}
