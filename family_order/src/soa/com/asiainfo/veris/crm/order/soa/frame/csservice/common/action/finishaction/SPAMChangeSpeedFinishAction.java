package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import org.apache.log4j.Logger;

public class SPAMChangeSpeedFinishAction implements ITradeFinishAction{

	public static final Logger logger=Logger.getLogger(SPAMChangeSpeedFinishAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception {

        logger.debug("========SPAMChangeSpeedFinishAction===");
		String tradeId=mainTrade.getString("TRADE_ID");
		IDataset tradesvcInfo=TradeUserInfoQry.getTradeChangeSpeedDiscntById(tradeId);
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IDataset userinfo = UserInfoQry.getUserinfo("KD_"+serialNumber);
        logger.debug("========SPAMChangeSpeedFinishAction===userinfo:"+userinfo);
        if (IDataUtil.isNotEmpty(userinfo)) {
            String userId = userinfo.getData(0).getString("USER_ID");
            logger.debug("========SPAMChangeSpeedFinishAction===tradesvcInfo:"+tradesvcInfo);
            if(tradesvcInfo!=null){
                for(int i=0;tradesvcInfo.size()>i;i++){
                    String discnt_code=tradesvcInfo.getData(i).getString("DISCNT_CODE");
                    String modify_tag=tradesvcInfo.getData(i).getString("MODIFY_TAG");
                    logger.debug("========SPAMChangeSpeedFinishAction===discnt_code:"+discnt_code);
                    if("80176874".equals(discnt_code)&&("1".equals(modify_tag)||("U".equals(modify_tag)))){
                        //调用服开变更速率接口 降速
                        logger.debug("========SPAMChangeSpeedFinishAction===down");
                        changeResMethod(tradeId, userId, "KD_"+serialNumber,
                                WideNetUtil.getWidenetUserRate(serialNumber),"307200");
                    }else if("80176874".equals(discnt_code)&&"0".equals(modify_tag)){
                        //调用服开变更速率接口 升速
                        logger.debug("========SPAMChangeSpeedFinishAction===up");
                        changeResMethod(tradeId, userId, "KD_"+serialNumber,
                                "307200", WideNetUtil.getWidenetUserRate(serialNumber));
                    }
                }

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
			logger.error("SPAMChangeSpeedFinishAction异常"+e);
		}
	}

}
