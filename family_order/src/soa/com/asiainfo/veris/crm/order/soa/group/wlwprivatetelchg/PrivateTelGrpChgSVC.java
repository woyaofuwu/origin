package com.asiainfo.veris.crm.order.soa.group.wlwprivatetelchg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PrivateTelGrpChgSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public IDataset getElements(IData param)throws Exception{
		PrivateTelGrpChgBean bean = new PrivateTelGrpChgBean();
		return bean.getElements(param);
	}
	
	
	public IDataset getattrs(IData param) throws Exception{
		PrivateTelGrpChgBean bean = new PrivateTelGrpChgBean();
		return bean.getattrs(param);
	}
}
