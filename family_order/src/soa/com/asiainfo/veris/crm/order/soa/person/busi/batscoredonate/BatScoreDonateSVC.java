package com.asiainfo.veris.crm.order.soa.person.busi.batscoredonate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class BatScoreDonateSVC extends CSBizService {
	
	 public void importBatData(IData input) throws Exception
	 {
		 BatScoreDonateBean bean = (BatScoreDonateBean) BeanManager.createBean(BatScoreDonateBean.class);
		 bean.importBatData(input);
	 }
	 
	 public void dealSubmit(IData input) throws Exception
	 {
		 BatScoreDonateBean bean = (BatScoreDonateBean) BeanManager.createBean(BatScoreDonateBean.class);
		 bean.dealSubmit(input);
	 }
	 
	 public IDataset queryImportData(IData input) throws Exception
	 {
		 BatScoreDonateBean bean = (BatScoreDonateBean) BeanManager.createBean(BatScoreDonateBean.class);
		 return bean.queryImportData(input,getPagination());
	 }

}
