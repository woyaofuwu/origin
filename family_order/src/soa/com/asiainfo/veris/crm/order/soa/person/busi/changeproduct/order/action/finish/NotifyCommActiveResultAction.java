package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

/**
 * 6.57.3 通用类活动办理结果通知接口
 * @author 13198
 *
 */
public class NotifyCommActiveResultAction implements ITradeFinishAction {
	
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		IDataset discntTradeData = TradeDiscntInfoQry.getTradeDiscntByTradeId(mainTrade.getString("TRADE_ID",""));
        if (IDataUtil.isEmpty(discntTradeData)) {
            return;
        }
        IDataset saleActiveTradeData = TradeSaleActive.getTradeSaleActiveByTradeId(mainTrade.getString("TRADE_ID",""));
        if (IDataUtil.isEmpty(saleActiveTradeData)) {
            return;
        }
        
        IData param = new DataMap();
		// 1. 填入 同步接口的同步结果
		param.put("TRANS_IDO", saleActiveTradeData.getData(0).getString("RSRV_STR17",""));
		param.put("RET_CODE", "00");
		String isCommActive = saleActiveTradeData.getData(0).getString("RSRV_STR20","");
		if("1".equals(isCommActive)){
			param.put("KIND_ID", "BIP3A286_T3000287_0_0");
			for(int i=0;i<discntTradeData.size();i++){
	        	String modifyTag = discntTradeData.getData(i).getString("MODIFY_TAG","");
		       	String discntCode = discntTradeData.getData(i).getString("DISCNT_CODE","");
		       	Date startDate = SysDateMgr.string2Date(discntTradeData.getData(i).getString("START_DATE",""), SysDateMgr.PATTERN_STAND);
		       	String startTime = SysDateMgr.date2String(startDate, SysDateMgr.PATTERN_STAND_SHORT);
		    	Date endDate = SysDateMgr.string2Date(discntTradeData.getData(i).getString("END_DATE",""), SysDateMgr.PATTERN_STAND);
		       	String endTime = SysDateMgr.date2String(endDate, SysDateMgr.PATTERN_STAND_SHORT);
		       	if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
		       			       			
		       		IDataset commparaList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "2001", "COMM_ACTIVE_GIFT", discntCode);
		        	if(IDataUtil.isNotEmpty(commparaList)){
		        	
			        	// 2. 赠送权益信息 
			     		String discntId = commparaList.getData(0).getString("PARA_CODE1");
			     		String GOOD_PRICE = commparaList.getData(0).getString("PARA_CODE2","");
			     		String GOOD_NUM = commparaList.getData(0).getString("PARA_CODE3");
			     		String UNIT = commparaList.getData(0).getString("PARA_CODE4");
			     		
			     		IDataset activityInfo = new DatasetList();
			     		IData info = new DataMap();
			     		info.put("ACTIVITY_ID", saleActiveTradeData.getData(0).getString("RSRV_STR18"));
			     		info.put("ACTIVITY_NAME", saleActiveTradeData.getData(0).getString("RSRV_STR19"));
			     		info.put("GOOD_ID", discntId);
			     		
			     		IDataset list = UpcCall.queryOfferNameByOfferCodeAndType("D", discntId);
			     		if(IDataUtil.isNotEmpty(list)){
			     			info.put("GOOD_NAME", list.getData(0).getString("OFFER_NAME",""));
			     		}else {
			     			info.put("GOOD_NAME", "");
			     		}
			     		info.put("GOOD_PRICE", GOOD_PRICE);
			     		info.put("GOOD_NUM", GOOD_NUM);
			     		info.put("UNIT", UNIT);
			     		info.put("GOOD_VALID_TIME", startTime);
			     		info.put("GOOD_EXPIRE_TIME", endTime);
			     		activityInfo.add(info);
			     		param.put("ACTIVITY_INFO", activityInfo);
		        	}
		       	}
	        }
			
			try{
				// 3. 通过IBOSS 传至 一级电渠
				if((param.getDataset("ACTIVITY_INFO")).isEmpty()){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取赠送权益信息为空！");
					/*param.put("RET_CODE", "01");
					param.put("RET_RESC", "获取赠送权益信息为空");*/
				}
				IDataset res = IBossCall.dealInvokeUrl("BIP3A286_T3000287_0_0", "IBOSS", param);
				
			}catch(Exception e){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "IBOSS调用异常！");
			}
		}
	}

}
