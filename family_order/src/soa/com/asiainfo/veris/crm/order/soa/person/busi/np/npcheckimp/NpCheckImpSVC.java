package com.asiainfo.veris.crm.order.soa.person.busi.np.npcheckimp;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class NpCheckImpSVC extends CSBizService{
	
	 public IDataset queryImportData(IData input) throws Exception
	 {
		 NpCheckImpBean bean = (NpCheckImpBean) BeanManager.createBean(NpCheckImpBean.class);
		return bean.queryImportData(input,getPagination());
	 }
	 
	 public boolean modifyData(IData input) throws Exception
	 {
		 NpCheckImpBean bean = (NpCheckImpBean) BeanManager.createBean(NpCheckImpBean.class);
		return bean.modifyData(input);
	 }

}
