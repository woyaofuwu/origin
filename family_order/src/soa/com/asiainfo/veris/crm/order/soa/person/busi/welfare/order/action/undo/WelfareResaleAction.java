
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.action.undo;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

public class WelfareResaleAction implements ITradeFinishAction
{
	static final Logger logger = Logger.getLogger(WelfareResaleAction.class);
	
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
    	String userId = mainTrade.getString("USER_ID");
    	String serialNumber = mainTrade.getString("SERIAL_NUMBER");
    	String tradeId = mainTrade.getString("TRADE_ID");
    	////////////////开户返销处理/////////////////////////////////////////////////////////
		if("10".equals(tradeTypeCode)){
			//查询用户第一笔151台账
			IDataset minTradeQ = TradeHistoryInfoQry.queryMinTradeByUserIdAndTradeTypeCode(userId, WelfareConstants.TradeType.ACCEPT.getValue());
			if(IDataUtil.isNotEmpty(minTradeQ)){
				//只要有权益在，就给权益USER_ID和SERIAL_NUMBER把该用户的所有权益都取消，不用一个一个调用
				//入参拼接
				IData abilityData = new DataMap();
				IDataset userInfoDataSet = new DatasetList();
				IData orderVos = new DataMap();
				orderVos.put("userId", userId);
				orderVos.put("userType", "0");//0代表个人业务
				orderVos.put("telnum", serialNumber);
				userInfoDataSet.add(orderVos);
				
				abilityData.put("operType", "2");//2代表销户
				abilityData.put("returnOrderVos", userInfoDataSet);
				
				//能开接口调用
				IData abilityResult = abilityMethod(abilityData);
				String respCode=abilityResult.getString("respCode");
				if("0000".equals(respCode)){
					//开户的tradeId
					String tradeIdCpu = minTradeQ.getData(0).getString("RSRV_STR2");
					//权益办理的tradeId
					String tradeIdQ = minTradeQ.getData(0).getString("TRADE_ID");
					if(StringUtils.isNotBlank(tradeIdCpu) && tradeIdCpu.equals(tradeId)){
						//走返销登记，返销权益数据
						IData pdData = new DataMap();
		                pdData.put("REMARKS", "开户返销，返销权益资料");
		                pdData.put("TRADE_ID", tradeIdQ);
		                pdData.put("IS_CHECKRULE", false);
		                pdData.put(Route.ROUTE_EPARCHY_CODE,mainTrade.getString("TRADE_EPARCHY_CODE","0898"));
		                CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
					}
					
				}else{
					IData out=abilityResult.getData("respDesc");
		    		CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益取消返回失败信息"+out);
				}
			}
			
    	}
		////////////////预约产品变更取消处理/////////////////////////////////////////////////////////
		if("110".equals(tradeTypeCode)){//AQ  BQ  
    		IDataset tradeOfferRelInfos = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);
    		if(IDataUtil.isNotEmpty(tradeOfferRelInfos)){
    			for(int i = 0;i<tradeOfferRelInfos.size();i++){
    				IData offerRelInfo = tradeOfferRelInfos.getData(i);
    				if("Q".equals(offerRelInfo.getString("REL_OFFER_TYPE"))){
    					String modifyTag = offerRelInfo.getString("MODIFY_TAG");
        				if("1".equals(modifyTag)){
        					//能开接口调用（办理权益）
    						IData abilityResult = abilityMethod(offerRelInfo);
    						String respCode=abilityResult.getString("respCode");
    						if(!"0000".equals(respCode)){
    							IData out=abilityResult.getData("result");
    				    		String X_RESPCODE=out.getString("respCode");
    				    		String X_RESPDESC=out.getString("respDesc");
    				    		CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益取消返回失败信息"+X_RESPCODE+X_RESPDESC);
    						}
        				}
        				if("0".equals(modifyTag)){
        					//能开接口调用（取消权益）
    						IData abilityResult = abilityMethod(offerRelInfo);
    						String respCode=abilityResult.getString("respCode");
    						if(!"0000".equals(respCode)){
    							IData out=abilityResult.getData("result");
    				    		String X_RESPCODE=out.getString("respCode");
    				    		String X_RESPDESC=out.getString("respDesc");
    				    		CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益取消返回失败信息"+X_RESPCODE+X_RESPDESC);
    						}
        				}
    					
    				}
    				
    			}

    		}
    		
    	}

    }
    //能开接口调用
    public IData abilityMethod(IData abilityData) throws Exception{
    	
    	IData retData = new DataMap();
    	logger.debug("=============接口入参："+abilityData);
		try {
			//调用能开接口url
			String Abilityurl = BizEnv.getEnvString("crm.ABILITY.CIP117");
			if (StringUtils.isBlank(Abilityurl)) {
				CSAppException.appError("-1", "crm.ABILITY.CIP117接口地址未在TD_S_BIZENV表中配置");
			}
			String apiAddress = Abilityurl;
			//调用能力开放平台接口返回数据
			retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
			logger.debug("=============能开返回参数 "+retData);
			
		} catch (Exception e) {
			logger.error("WelfareResaleAction exception:" + e.getMessage());
			//调用权益接口失败
			CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益通知接口失败");
		}
		
		return retData;
		
    }

}
