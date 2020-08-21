package com.asiainfo.veris.crm.order.soa.person.busi.giveuserclass;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GiveUserClassSVC extends CSBizService {

	public IDataset queryUserClassBySn(IData data) throws Exception {
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		IDataset rds = bean.queryUserClassBySn(data);
		return rds;
	}

	public IDataset queryUserClassDetailBySn(IData data) throws Exception {
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		IDataset rds = bean.queryUserClassDetailBySn(data);
		return rds;
	}

	// 新增成员时候的校验
	public IDataset checkAddMeb(IData data) throws Exception {
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		return bean.checkAddMeb(data);
	}

	public IDataset submitData(IData data) throws Exception {
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		return bean.submitData(data);
	}
	
	public IData submitDataIntf(IData data) throws Exception {
		GiveUserClassBean bean = BeanManager.createBean(GiveUserClassBean.class);
		return bean.submitData(data).first();
	}
}
