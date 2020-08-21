package com.asiainfo.veris.crm.order.soa.person.busi.balance;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BalanceAlertSVC extends CSBizService{
	private static final long serialVersionUID = 1L;

	//查询是否开通余额提醒服务，如果开通则返回提醒金额
	public IData isOpenBalanceAlert(IData data) throws Exception{
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SERVICE_ID");
		
		String serialnumber = data.getString("SERIAL_NUMBER");//手机号码
		String serviceid = data.getString("SERVICE_ID");//服务号码
		
		String isOpenFlag = "0";//是否开通余额提醒标识 0代表未开通 1代表开通
		
	    IData userInfo = new DataMap();
	    IDataset users = UserInfoQry.getUserInfoBySerailNumber("0",serialnumber);
	    
	    if(IDataUtil.isNotEmpty(users)){
	    	userInfo = users.getData(0);
	    }
	    
	    IDataset retSet = UserAttrInfoQry.getAutoPayContractInfo(userInfo.getString("USER_ID"),serviceid);
	    
	    IData result = new DataMap();
	    
	    if(IDataUtil.isNotEmpty(retSet)){
	    	 isOpenFlag = "1";
	    	 for(int a=0;a<retSet.size();a++){
	    		String code = retSet.getData(a).getString("ATTR_CODE");
	    				if(code.equals("100098")){
	    					 result.put("SENDTYPE", retSet.getData(a).getString("ATTR_VALUE",""));
	    				}if(code.equals("100099")){
	    					result.put("BALANCE_COUNT", retSet.getData(a).getString("ATTR_VALUE",""));//余额提醒金额
	    				}
	    	 }
	    	 //result.put("X_RECORDNUM", retSet.size());
	    }
	    /**
	     else{
	    	result.put("X_RECORDNUM", 0);
	    	result.put("BALANCE_COUNT", 0);//余额提醒金额
	    }
	    result.put("X_RESULTCODE", "0");
	    result.put("X_RESULTINFO", "OK");
	    **/
        result.put("ISOPENFLAG", isOpenFlag);
		return result;
	}
	
}
