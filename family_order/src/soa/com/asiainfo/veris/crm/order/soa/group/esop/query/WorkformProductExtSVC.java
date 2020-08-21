package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class WorkformProductExtSVC extends CSBizService {

	private static final long serialVersionUID = 1L;
	
	public IDataset qryProductByIbsysid(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		return WorkformProductExtBean.qryProductByIbsysid(ibsysid);
	}
	
	public IData qryProductByrecodeNum(IData param) throws Exception{
		String ibsysid = param.getString("IBSYSID");
		String recordNum = param.getString("RECORD_NUM");
		return WorkformProductExtBean.qryProductByrecodeNum(ibsysid, recordNum);
	}
	
}
