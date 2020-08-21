package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BusinessTvSvc extends CSBizService{

	public IData findTvList(IData input) throws Exception{
		BusinessTvBean bean = new BusinessTvBean();
		return bean.findTvList(input);
	}
}
