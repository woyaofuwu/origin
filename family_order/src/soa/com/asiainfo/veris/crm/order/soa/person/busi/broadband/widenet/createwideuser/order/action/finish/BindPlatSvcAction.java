package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class BindPlatSvcAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		String serialNumber = mainTrade.getString("SERIAL_NUMBER").replace("KD_", "");
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isNotEmpty(userInfo)){
		   IDataset tradeOherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId,"BIND_PLATSVC");
			IDataset ids = new DatasetList();
		   for(int i=0;i<tradeOherInfos.size();i++){
			   String serviceId = tradeOherInfos.getData(i).getString("RSRV_STR3");
			   IDataset platInfos = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
			   IDataset userPlatSvcDataset = UserPlatSvcInfoQry.qryPlatSvcByUserIdServiceId(userInfo.getString("USER_ID"), serviceId);
			   //用户不存在平台服务才办理
			   if(IDataUtil.isNotEmpty(platInfos) && IDataUtil.isEmpty(userPlatSvcDataset)){
				   IData data = new DataMap();
				   if("600".equals(tradeTypeCode)){
					   data.put("START_DATE",SysDateMgr.getSysTime());
				   }
				   if("601".equals(tradeTypeCode)){
					   //根据tradeId查询产品预约时间
					   IDataset proDatasetData = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
					   for(int j=0;i<proDatasetData.size();j++){
						   String modifyTag = proDatasetData.getData(j).getString("MODIFY_TAG");
						   if("0".equals(modifyTag)){
							   data.put("START_DATE", proDatasetData.getData(j).getString("START_DATE"));
							   break;
						   }
					   } 
				   }
				   data.put("SERVICE_ID", serviceId);
				   data.put("SP_CODE", platInfos.getData(0).getString("SP_CODE", ""));
				   data.put("BIZ_CODE", platInfos.getData(0).getString("BIZ_CODE", ""));
				   data.put("BIZ_TYPE_CODE", platInfos.getData(0).getString("BIZ_TYPE_CODE", ""));
				   data.put("MODIFY_TAG", "0");
				   data.put("OPER_CODE", PlatConstants.OPER_ORDER);
				   ids.add(data);
			   }
			   
		   }
		   if(IDataUtil.isNotEmpty(ids)){
			   IData input = new DataMap();
			   input.put("SERIAL_NUMBER",serialNumber);
			   input.put("USER_ID",userInfo.getString("USER_ID"));
			   input.put("SELECTED_ELEMENTS",ids);
			   input.put("NO_TRADE_LIMIT", "TRUE");
			   input.put("SKIP_RULE", "TRUE");
			   input.put("REMARK","根据other台账办理平台服务");
			   IData result = CSAppCall.call("SS.PlatRegSVC.tradeReg", input).first();
			   if (StringUtils.isNotBlank(result.getString("ORDER_ID")) || StringUtils.isNotBlank(result.getString("TRADE_ID")))
			   {

			   }
		   }

    		
    	}
     
    }

}
