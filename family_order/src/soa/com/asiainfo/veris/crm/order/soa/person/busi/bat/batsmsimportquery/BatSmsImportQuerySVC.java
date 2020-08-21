package com.asiainfo.veris.crm.order.soa.person.busi.bat.batsmsimportquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unfinishtrade.QueryUnfinishTradeBean;

public class BatSmsImportQuerySVC extends CSBizService{

	  public IDataset batSmsImportQuery(IData data) throws Exception
	    {
		  BatSmsImportQueryBean bean = (BatSmsImportQueryBean) BeanManager.createBean(BatSmsImportQueryBean.class);
	        return bean.batSmsImportQuery(data, getPagination());
	    }
 //1
}
