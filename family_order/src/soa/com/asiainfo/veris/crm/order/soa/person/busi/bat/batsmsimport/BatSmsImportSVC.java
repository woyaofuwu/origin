package com.asiainfo.veris.crm.order.soa.person.busi.bat.batsmsimport;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BatSmsImportSVC extends CSBizService{

	public IDataset getWellNumList(IData inparams) throws Exception{
		
		BatSmsImportBean bean = BeanManager.createBean(BatSmsImportBean.class);
		return bean.getWellNumList();
		
	}
	
	
}
