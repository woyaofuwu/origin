package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpuser;


import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CheckBeforeNpUserSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -104749738394309391L;

	/**
	 *查验申请
	 */
	public IData insUserNpCheck(IData input) throws Exception {
		CheckBeforeNpUserBean bean = BeanManager.createBean(CheckBeforeNpUserBean.class);
		return bean.insUserNpCheck(input);
	}
	
}
