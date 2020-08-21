package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.staffdept;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class StaffDeptInfoQrySVC extends CSBizService{

	public IDataset getStaffList(IData cond) throws Exception
	{
		String staffId = cond.getString("STAFF_ID");
		String staffName = cond.getString("STAFF_NAME");
		String departCode = cond.getString("DEPART_CODE");
		String departName = cond.getString("DEPARTnAME");
		return StaffDeptInfoQry.getStaffList(staffId, staffName, departCode, departName, getPagination());
	}
	
	public IData getStaffInfo(IData cond) throws Exception{
		String staffId = cond.getString("STAFF_ID");
		IData param = new DataMap();
		param.put("STAFF_ID", staffId);
		return StaffDeptInfoQry.getStaffInfo(param);
	}
}
