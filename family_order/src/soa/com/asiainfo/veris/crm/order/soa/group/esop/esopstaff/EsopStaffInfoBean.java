package com.asiainfo.veris.crm.order.soa.group.esop.esopstaff;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;

public class EsopStaffInfoBean {

	public static boolean qryStaffOtherInfos(IData param,IData result) throws Exception{
		String staffId=param.getString("STAFF_ID");
		String LoginId=param.getString("LOGIN_LOG_ID");
    	//step1:获取登陆情况
		IDataset logds = EsopStaffInfoDAO.qryStaffLog(staffId, LoginId); 
		if(DataUtils.isNotEmpty(logds)){
			if("".equals(logds.first().getString("OUT_TIME",""))&&param.getString("RID","").equals(logds.first().getString("RID"))){
    			result.put("VALID", "true");    	
    		}else{
    			result.put("X_RESULTINFO","员工未登陆！");
    			result.put("VALID", "false");
    			return false;
    		}
		}
    	
    	//step2:获取员工的下级员工
    	IDataset managerds = EsopStaffInfoDAO.qryByStaffManager(staffId);
    	if(DataUtils.isNotEmpty(managerds)){
    		String tempManagerStr="";
    		for(int i=0;i<managerds.size();i++){
    			String s = managerds.get(i,"STAFF_ID").toString();
    			tempManagerStr+=s+ ((i!=managerds.size()-1)?",":"");
    		}
    		result.put("subStaffIds", tempManagerStr);
    	}
    	
    	//step3:获取员工数据角色
    	IDataset datarightds = EsopStaffInfoDAO.qryStaffDataRight(staffId);
    	if(DataUtils.isNotEmpty(datarightds)){
    		String tempDatarightStr="";
    		for(int i=0;i<datarightds.size();i++){
    			String s = datarightds.get(i,"DATA_CODE").toString();
    			tempDatarightStr+=s+ ((i!=datarightds.size()-1)?",":"");
    		}
    		result.put("jobIds", tempDatarightStr);
    	}
    	
    	return true;
	}
}
