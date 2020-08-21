package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;

public class OrderFreeDiscntFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String actionType = mainTrade.getString("RSRV_STR1",""); // 0:购买 1:换机
		
		if(actionType!=null&&actionType.equals("0")){
			String tradeId=mainTrade.getString("TRADE_ID");
			
			IDataset platSvcInfos=TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
			
			if(IDataUtil.isNotEmpty(platSvcInfos)){
				
				//获取当前时间，并验证当前是25号之前，还是之后
				//REQ201803260008开户首月免费优化:取消25号的判断
				/*boolean isBefore25=true;
				String curDay=SysDateMgr.getCurDay();
				
				if(curDay.compareTo("25")>=0){
					isBefore25=false;
				}*/
				
				//获取要办理的套餐信息
				List<String> discntCodes=new ArrayList<String>();
				for(int i=0,size=platSvcInfos.size();i<size;i++){
					IData platSvcInfo=platSvcInfos.getData(i);
					//String serviceId=platSvcInfo.getString("SERVICE_ID","");
					if(platSvcInfo.getString("MODIFY_TAG","").equals(BofConst.MODIFY_TAG_ADD)){
						//查询服务对应的首免优惠信息
						IDataset discntConfigs=CommparaInfoQry.queryComparaByAttrAndCode1
								("CSM", "4022", "TOP_SET_OPEN_DISCNT", "4022");
					
						if(IDataUtil.isNotEmpty(discntConfigs)){
							String discntCode=discntConfigs.getData(0).getString("PARA_CODE2","");
							
							if(!discntCodes.contains(discntCode)){
								discntCodes.add(discntCode);
							}
						}else{
							if(!discntCodes.contains("40229999")){
								discntCodes.add("40229999");
							}
						}
						
						break;
						
					}
				}
				
				
				if(discntCodes!=null&&discntCodes.size()>0){
					
					//在tf_b_trade表里intf_id中添加TF_B_TRADE_DISCNT,才能执行优惠信息
					String intfId=mainTrade.getString("INTF_ID","");
					if(intfId.indexOf("TF_B_TRADE_DISCNT,")==-1){
						TradeInfoQry.updateTradeIntfId(mainTrade.getString("TRADE_ID"), "TF_B_TRADE_DISCNT,");
					}
					
					for(int i=0,size=discntCodes.size();i<size;i++){
						String discntCode=discntCodes.get(i);
						
						IData discntTradeData=new DataMap();
						discntTradeData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
						discntTradeData.put("USER_ID", mainTrade.getString("USER_ID"));
						discntTradeData.put("USER_ID_A", "-1");
						discntTradeData.put("PACKAGE_ID", "-1");
						discntTradeData.put("PRODUCT_ID", "-1");
						discntTradeData.put("DISCNT_CODE", discntCode);
						discntTradeData.put("SPEC_TAG","0");
						discntTradeData.put("INST_ID", SeqMgr.getInstId());
						discntTradeData.put("START_DATE", mainTrade.getString("ACCEPT_DATE"));
						
						//REQ201803260008开户首月免费优化:取消25号的判断
						discntTradeData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						/*if(isBefore25){	//如果是25号前，就是本月底失效
							discntTradeData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
						}else{	//如果是25号后，就是下月底失效
							discntTradeData.put("END_DATE", SysDateMgr.getNextMonthLastDate());
						}*/
						discntTradeData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
						discntTradeData.put("UPDATE_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
						discntTradeData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
						discntTradeData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
						discntTradeData.put("REMARK", mainTrade.getString("互联网电视开户绑定首免套餐"));
						
						TradeDiscntInfoQry.saveDiscntTradeForTopSetOpen(discntTradeData);
						
					}
				}
			}
		}
	}
	
}
