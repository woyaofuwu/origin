package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

public class StopIMSAction implements ITradeAction{
	
	private static transient Logger logger = Logger.getLogger(StopIMSAction.class);
	

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String  serialNumber=btd.getRD().getUca().getSerialNumber().replace("KD_", "");
        String userIdB="";
        if(!"".equals(serialNumber) && serialNumber !=null){
       	 IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber); 
       	 if(IDataUtil.isNotEmpty(userInfoData)){
       		 userIdB =  userInfoData.getString("USER_ID","").trim();
       	 }else{
       		return;
       	 }
        }
        //获取主号信息
        IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "1");
        if(IDataUtil.isNotEmpty(iDataset)){
       	 //获取虚拟号
       	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
       	 //通过虚拟号获取关联的IMS家庭固话号码信息
       	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");       	 
       	 
	       	 if(IDataUtil.isNotEmpty(userBInfo)){
	       		IData ImsInfo =userBInfo.getData(0);
	       		IDataset userSvcStateInfo= UserSvcStateInfoQry.getUserMainState(ImsInfo.getString("USER_ID_B"));
	       		if(IDataUtil.isNotEmpty(userSvcStateInfo)){
	       			String stateCode=userSvcStateInfo.getData(0).getString("STATE_CODE", "");
		       	    if("0".equals(stateCode)){
		       	    	
		       	    	IData data = new DataMap();
			       		data.put("TRADE_TYPE_CODE", "9807");
						//IMS家庭固话 userid
			    		String  userIdIMS=ImsInfo.getString("USER_ID_B", "");
			    		//IMS家庭固话   手机号码
			    		String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
			    		
			    		data.put("SERIAL_NUMBER", serialNumberB);
			    		data.put("USER_ID", userIdIMS);
			    		//为了不执行bre
			    		data.put("X_CHOICE_TAG", "1");
			    		data.put("NO_TRADE_LIMIT", "TRUE");
			    		CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", data);
			    		  
		       		}
	       			
	       		}
	       		
	       	 }
        }
		
	}

}
