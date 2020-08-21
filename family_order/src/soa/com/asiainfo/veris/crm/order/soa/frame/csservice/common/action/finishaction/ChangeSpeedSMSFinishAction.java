package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

/**
 * 提速包优惠办理完成 短信发送提醒
 * @author xuzh5
 *2018-9-19 0:41:20
 */
public class ChangeSpeedSMSFinishAction implements ITradeFinishAction{

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		String trade_id=mainTrade.getString("TRADE_ID");
		String serialNumber=mainTrade.getString("SERIAL_NUMBER");
		if(serialNumber.startsWith("KD_")){
			serialNumber=serialNumber.substring(3);
		}
		String msg="";
		IDataset tradesvcInfo=TradeUserInfoQry.getTradeSvcById(trade_id);
		//PL_DEAL 批量办理提速包不需要发送短信
		if(tradesvcInfo!=null && !"1".equals(mainTrade.getString("RSRV_STR5",""))){
			for(int i=0;tradesvcInfo.size()>i;i++){
				String discnt_code=tradesvcInfo.getData(i).getString("DISCNT_CODE");
				String modify_tag=tradesvcInfo.getData(i).getString("MODIFY_TAG");
				
				IDataset dataset=CommparaInfoQry.getCommparaByCodeCode1("CSM", "919", modify_tag, discnt_code);
				if(dataset.size()>0)
					msg=dataset.getData(0).getString("PARA_CODE20");
				
				//发送短信通知
    			IData inparam = new DataMap();
    	        inparam.put("NOTICE_CONTENT", msg);
    	        inparam.put("RECV_OBJECT", serialNumber);
    	        inparam.put("RECV_ID", serialNumber);
    	        inparam.put("REMARK", "提速包优惠短信发送");
    	        if(msg.length()>0)
    	        SmsSend.insSms(inparam);
			
				
			}
			
			
		}
		
	}
	

}
