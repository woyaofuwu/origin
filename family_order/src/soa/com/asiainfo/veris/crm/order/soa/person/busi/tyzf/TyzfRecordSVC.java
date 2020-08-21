package com.asiainfo.veris.crm.order.soa.person.busi.tyzf;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.ibossquery.QueryIbTradeBean;

public class TyzfRecordSVC extends CSBizService{
	private static final long serialVersionUID = 1L;
	public IDataset queryData(IData data)throws Exception {
		TyzfRecordBean bean = BeanManager.createBean(TyzfRecordBean.class);
        return bean.queryData(data, this.getPagination());
	}

}
