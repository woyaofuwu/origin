package com.asiainfo.veris.crm.order.soa.person.busi.cpelist;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.cpelist.CpeListBean;

public class CpeListSVC extends CSBizService {
	
	 private static final long serialVersionUID = 1L;
	 private static Logger logger = Logger.getLogger(CpeListSVC.class);
	 
		 public IDataset queryImportData(IData input) throws Exception
		 {
			CpeListBean bean = (CpeListBean) BeanManager.createBean(CpeListBean.class);
			return bean.queryImportData(input,getPagination());
		 }


}
