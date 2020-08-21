package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultistatuschange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SingleNumMultiDeviceStatusChangeSVC extends CSBizService {
	private static final long serialVersionUID = 2866237306813883199L;
	
	/**
	 * 当前有效的副设备列表查询供展示
	 */
	public IDataset queryAuxDevInfos(IData input) throws Exception {
		SingleNumMultiDeviceStatusChangeBean bean = (SingleNumMultiDeviceStatusChangeBean) BeanManager.createBean(SingleNumMultiDeviceStatusChangeBean.class);
		return bean.queryAuxDevInfos(input);
	}
	

}