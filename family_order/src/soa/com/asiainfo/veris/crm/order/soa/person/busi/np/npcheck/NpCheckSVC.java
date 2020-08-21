package com.asiainfo.veris.crm.order.soa.person.busi.np.npcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npcheck.NpCheckBean;

public class NpCheckSVC extends CSBizService {

	 public IDataset queryImportData(IData input) throws Exception
	 {
		NpCheckBean bean = (NpCheckBean) BeanManager.createBean(NpCheckBean.class);
		return bean.queryImportData(input,getPagination());
	 }
	 
	 public int modifyState(IData input) throws Exception
	 {
		 NpCheckBean bean = (NpCheckBean) BeanManager.createBean(NpCheckBean.class);
		return bean.modifyState(input);
	 }
	 
	 public void importBatData(IData input) throws Exception
	 {
		 NpCheckBean bean = (NpCheckBean) BeanManager.createBean(NpCheckBean.class);
		 bean.importBatData(input);
	 }
}
