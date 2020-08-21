package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;

public class DiscntReserVationFinishAction implements ITradeFinishAction {

	protected static final Logger log = Logger.getLogger(DiscntReserVationFinishAction.class);
	
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String userID = mainTrade.getString("USER_ID");
		String tradeID = mainTrade.getString("TRADE_ID");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
		
		//获取当前时间
		String timeSet =SysDateMgr.decodeTimestamp(SysDateMgr.getSysDateYYYYMMDDHHMMSS(),"yyyy-MM-dd");
		
		//查询用户服务台账
		IDataset svcDataset= TradeSvcInfoQry.getTradeSvcByTradeId(tradeID); 
		//是否存在指定服务
		if (IDataUtil.isNotEmpty(svcDataset)) {
			for (int z = 0; z < svcDataset.size(); z++) {
				String svcID = svcDataset.getData(z).getString("SERVICE_ID");
				
				String mod_tag = svcDataset.getData(z).getString("MODIFY_TAG");
				
				if ("84071642".equals(svcID) && mod_tag.equals(BofConst.MODIFY_TAG_ADD)) {
					//发起解限速指令
			        IData pccParam = new DataMap();
			        pccParam.put("USER_ID", userID);
			        pccParam.put("OPERATOR_MIND", "3");
			        pccParam.put("REMARK", "达量限速解速");
			        pccParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
			        try{
			        	CSAppCall.call("SS.PccActionSVC.pccIntf", pccParam);
			        
			        }catch(Exception ex){
			        	log.error("pccErr= "+ex.getMessage());
			        }
			        
			        return;
				}
				
			}
		}
		
		//
		
		//查询用户优惠台账
		IDataset iDataset= TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeID); 
		if (IDataUtil.isNotEmpty(iDataset)) {
			for (int i = 0; i < iDataset.size(); i++) {
				String discnt_code = iDataset.getData(i).getString("DISCNT_CODE");
				//优惠开始时间
				String startDate = SysDateMgr.decodeTimestamp(iDataset.getData(i).getString("START_DATE"),"yyyy-MM-dd");
				
				String mod_tag = iDataset.getData(i).getString("MODIFY_TAG");
				
				//是否存在指定优惠
				IDataset offers = CommparaInfoQry.getCommparaInfoBy5("CSM", "9108", "RESTRICTIVE_DISCNT", discnt_code ,CSBizBean.getTradeEparchyCode(),null);
				boolean isTagid = false;
				if (IDataUtil.isNotEmpty(offers)) {
					isTagid=true;
				}
				
				
				if(isTagid){
					if (startDate.compareTo(timeSet)>0  && mod_tag.equals(BofConst.MODIFY_TAG_ADD) ) { //预约优惠时
						IData param = new DataMap();
						IData dealCond = new DataMap();
						dealCond.put("DISCNT_CODE", discnt_code);
			            param.put("DEAL_ID", SeqMgr.getTradeId());
			            param.put("USER_ID", userID);
			            param.put("PARTITION_ID", userID.substring(userID.length() - 4));
			            param.put("SERIAL_NUMBER", serialNumber);
			            param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
			            param.put("IN_TIME", SysDateMgr.getSysTime());
			            param.put("DEAL_STATE", "0");
			            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_RESERVATION_SVC);
			            param.put("EXEC_TIME", startDate);
			            param.put("DEAL_COND", dealCond.toString());
			            param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(timeSet));
			            param.put("TRADE_ID", tradeID); 
			            
			            Dao.insert("TF_F_EXPIRE_DEAL", param);
			            
					}else if(mod_tag.equals(BofConst.MODIFY_TAG_DEL) && startDate.compareTo(timeSet)>0){

				      //订单返销时，将未处理的到期处理记录失效
				       IData inparams = new DataMap();
						 inparams.put("USER_ID", userID);
						 inparams.put("DEAL_STATE", "0");
						 inparams.put("DEAL_TYPE",BofConst.EXPIRE_TYPE_RESERVATION_SVC);
						 IDataset expireDeals=BofQuery.queryExpireDealByUserId(inparams);
						 boolean isCancel =false;
						 if(IDataUtil.isNotEmpty(expireDeals)){
							 for (Iterator eit = expireDeals.iterator(); eit.hasNext();){
								 IData expireDeal = (IData) eit.next();
								 String dealCond=expireDeal.getString("DEAL_COND");
								 if(StringUtils.isNotBlank(dealCond)&&dealCond.contains(discnt_code)){
									 
									 Dao.delete("TF_F_EXPIRE_DEAL", expireDeal);
									 isCancel =true;
								 }
								 
								 if (isCancel) {
									 IData param = new DataMap();
										IData dealCond1 = new DataMap();
										dealCond1.put("DISCNT_CODE", discnt_code);
							            param.put("DEAL_ID", SeqMgr.getTradeId());
							            param.put("USER_ID", userID);
							            param.put("PARTITION_ID", userID.substring(userID.length() - 4));
							            param.put("SERIAL_NUMBER", serialNumber);
							            param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
							            param.put("IN_TIME", SysDateMgr.getSysTime());
							            param.put("DEAL_STATE", "2");
							            param.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_RESERVATION_SVC);
							            param.put("EXEC_TIME", startDate);
							            param.put("DEAL_COND", dealCond1.toString());
							            param.put("EXEC_MONTH", SysDateMgr.getMonthForDate(timeSet));
							            param.put("DEAL_RESULT", "预约产品取消，无须处理！");
							            param.put("TRADE_ID", tradeID);
							            
							            Dao.insert("TF_FH_EXPIRE_DEAL", param);
								}
							 }
						 } 			       
					}
					
					break;
				}
				
				
			}
		}
		
	}

}
