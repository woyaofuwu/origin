package com.asiainfo.veris.crm.order.soa.frame.csservice.common.action.finishaction;
import org.apache.log4j.Logger;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ChangeSpeedFinishAction implements ITradeFinishAction{

	public static final Logger logger=Logger.getLogger(ChangeSpeedFinishAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String trade_id=mainTrade.getString("TRADE_ID");
		IDataset tradesvcInfo=TradeUserInfoQry.getTradeSvcById(trade_id);

		if(tradesvcInfo!=null){
			for(int i=0;tradesvcInfo.size()>i;i++){
				String discnt_code=tradesvcInfo.getData(i).getString("DISCNT_CODE");
				String modify_tag=tradesvcInfo.getData(i).getString("MODIFY_TAG");
				
				if("84013441".equals(discnt_code)&&("1".equals(modify_tag)||("U".equals(modify_tag)))){
					//调用服开变更速率接口 降速
				    changeResMethod(trade_id, mainTrade.getString("USER_ID"), mainTrade.getString("SERIAL_NUMBER"),"51200", "102400");
				}else if("84013441".equals(discnt_code)&&"0".equals(modify_tag)){
					 //调用服开变更速率接口 升速
					 changeResMethod(trade_id, mainTrade.getString("USER_ID"), mainTrade.getString("SERIAL_NUMBER"),"102400", "51200");
					 
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
			logger.debug("ChangeSpeedFinishAction异常"+e);
		}
	}
	
	
	
	
	////////同步优惠时间到账务/////////
	   public void insTibDiscnt(String iv_sync_sequence, String userId) throws Exception
	    {
	        IDataset UserDiscntInfos=UserDiscntInfoQry.getSpecDiscnt(userId);
	        if(IDataUtil.isNotEmpty(UserDiscntInfos)){
	        	for(int i=0;i<UserDiscntInfos.size();i++){
	        		IData param =UserDiscntInfos.getData(i);
	                param.put("PRODUCT_ID", param.getString("PRODUCT_ID","-1"));
	                param.put("PACKAGE_ID", param.getString("PACKAGE_ID","-1"));
	                param.put("SYNC_SEQUENCE", iv_sync_sequence);
	                param.put("USER_ID", userId);
	        	    Dao.executeUpdateByCodeCode("TI_B_USER_DISCNT", "INS_TIBDISCNT_FROM_USERDISCNT", param,Route.getJourDb(BizRoute.getRouteId()));
	        	}
	        } 
	    }
	    
	    public void insTiSync(String iv_sync_sequence) throws Exception
	    {
	        IData synchInfoData = new DataMap();
	        synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
	        String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
	        synchInfoData.put("SYNC_DAY", syncDay);
	        synchInfoData.put("SYNC_TYPE", "0");
	        synchInfoData.put("TRADE_ID", "0");
	        synchInfoData.put("STATE", "0");
	        synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
	        synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
	        Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb(BizRoute.getRouteId()));
	    }
	    

}
