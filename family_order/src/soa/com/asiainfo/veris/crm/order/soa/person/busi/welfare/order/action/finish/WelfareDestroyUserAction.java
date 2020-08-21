
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.action.finish;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.BusinessAbilityCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

public class WelfareDestroyUserAction implements ITradeFinishAction
{

	static final Logger logger = Logger.getLogger(WelfareDestroyUserAction.class);
    public void executeAction(IData mainTrade) throws Exception
    {
    	logger.debug("======WelfareDestroyUserAction=======");
    	String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
    	String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
    	String userId = mainTrade.getString("USER_ID");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	logger.debug("======userInfo======="+userInfo);
    	if(IDataUtil.isNotEmpty(userInfo)){
    		if("192".equals(tradeTypeCode) || "7240".equals(tradeTypeCode) || "49".equals(tradeTypeCode)){
    			IData inparam = new DataMap();
    			inparam.put("USER_ID", userId);
    			IDataset dataQ = UserOfferRelInfoQry.qryUserOfferRelByUserIdAndRelOfferType(userId, WelfareConstants.RelType.WEFFARE.getValue());
    			logger.debug("======dataQ======="+dataQ);
    			if(IDataUtil.isNotEmpty(dataQ)){
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
    				//能开接口调用，销户权益取消通知
    				IData abilityResult = abilityMethod(abilityData,"HAIN_UNHT_QYreturnOrCreateOrder");
    				String respCode=abilityResult.getString("respCode");
    				
    				if(!"00000".equals(respCode)){
    					IData out=abilityResult.getData("respDesc");
    		    		CSAppException.apperr(WelfareException.CRM_WELFARE_10,"销户权益通知接口失败"+out);
    				}else{
    					IData result = abilityResult.getData("result");
    					String resultCode = result.getString("respCode");
    					String resultDesc = result.getString("respDesc");
    					logger.debug("======resultCode======="+resultCode);
    					if(!"0000".equals(resultCode)){
    						CSAppException.apperr(WelfareException.CRM_WELFARE_10,"权益返回失败信息："+resultDesc);
    					}
    					
    					
    				}
    			}
        	}
    	}

    }
    //能开接口调用
    public IData abilityMethod(IData abilityData,String param) throws Exception{
    	
    	IData retData = new DataMap();
		try {
			//调用能开接口url
			retData = BusinessAbilityCall.callBusinessCenterCommon(param, abilityData);
			logger.debug("=============能开返回参数 "+retData);
		} catch (Exception e) {
			//调用权益接口失败
			logger.debug("============接口错误信息 "+e.getMessage());
			CSAppException.apperr(WelfareException.CRM_WELFARE_10,"调用权益通知接口失败"+e.getMessage());
		}
		
		return retData;
		
    }

}
