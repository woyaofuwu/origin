package com.asiainfo.veris.crm.order.soa.person.busi.wlwprivatetelchg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PrivateTelChgSVC extends CSBizService{

	public IDataset getElements(IData param)throws Exception{
		PrivateTelChgBean bean = new PrivateTelChgBean();
		return bean.getElements(param);
	}
	
	public IDataset getattrs(IData param) throws Exception{
		PrivateTelChgBean bean = new PrivateTelChgBean();
		return bean.getattrs(param);
	}
}
