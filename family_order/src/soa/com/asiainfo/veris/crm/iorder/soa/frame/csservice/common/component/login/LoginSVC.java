package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.login;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class LoginSVC extends CSBizService {

	public IData getLoginInfo(IData param) throws Exception{
		return new LoginBean().getLoginInfo(param);
	}
	
	public IData getLoginExtendInfo(IData param) throws Exception{
		return new LoginBean().getExtendLoginInfo(param);
	}

	public IData getRelationInfo(IData param) throws Exception{
		return new LoginBean().getRelationInfo(param);
	}

	public IData isAgentStaff(IData param) throws Exception{
		String staffId = param.getString("STAFF_ID");
		IDataset resultList = null;//StaffInfoQry.getAgentInfoByStaffId(staffId);
		IData out = new DataMap();
		if(DataUtils.isNotEmpty(resultList)){
			out.put("IS_AGENT", "true");
		}else{
			out.put("IS_AGENT", "false");
		}
		
		return out;
	}
}
