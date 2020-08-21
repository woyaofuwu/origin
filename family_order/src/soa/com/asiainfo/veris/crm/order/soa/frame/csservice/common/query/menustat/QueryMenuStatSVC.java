package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.menustat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryMenuStatSVC extends CSBizService{
	
	public IDataset getMenus(IData cond) throws Exception
	{
		QueryMenuStatBean bean = (QueryMenuStatBean) BeanManager.createBean(QueryMenuStatBean.class);
		return bean.getMenus(cond);
	}

}
