
package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveend.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.finish.after.WidentedSpeedUpFinishAction;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class SaleActiveStopWidenitSpeedAction implements ITradeFinishAction
{
	public static final Logger logger=Logger.getLogger(WidentedSpeedUpFinishAction.class);
    public void executeAction(IData mainTrade) throws Exception
    {
        String productId = mainTrade.getString("RSRV_STR1","");
        String packgeId = mainTrade.getString("RSRV_STR2","");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER","");
		String tradeId = mainTrade.getString("TRADE_ID","");
		if(!(serialNumber.contains("KD_"))){
			serialNumber = "KD_"+serialNumber;
		}
		IDataset userinfo = UserInfoQry.getUserinfo(serialNumber);
		
		if (IDataUtil.isNotEmpty(userinfo)) {
	       String userId = userinfo.getData(0).getString("USER_ID");
           IDataset commParaInfo7979 = CommparaInfoQry.queryCommInfos("7979", "KDTS_RATE", productId, packgeId);
           if (IDataUtil.isNotEmpty(commParaInfo7979) && commParaInfo7979.size() > 0) {
			
			   String speed = commParaInfo7979.getData(0).getString("PARA_CODE3");
 
        	   //调服开宽带提速
        	   this.changeResMethod(tradeId, userId, serialNumber,  WideNetUtil.getWidenetUserRate(serialNumber),speed);
  
		   }
        
		}
    }

    
    public void changeResMethod(String TRADE_ID ,String USER_ID,String SERIAL_NUMBER ,String PRES_RATE,String OLD_PRES_RATE){		
		IData inParam = new DataMap();
		inParam.put("TRADE_ID", TRADE_ID);
		inParam.put("USER_ID", USER_ID);
		inParam.put("SERIAL_NUMBER", SERIAL_NUMBER);
		inParam.put("PRES_RATE", PRES_RATE);
		inParam.put("OLD_PRES_RATE", OLD_PRES_RATE);
    	try {
			CSAppCall.callNGPf("PF_ORDER_CHANGERES", inParam);
		} catch (Exception e) {			
			logger.error("SaleActiveStopWidenitSpeedAction==error== "+e);
		}
	}
}
