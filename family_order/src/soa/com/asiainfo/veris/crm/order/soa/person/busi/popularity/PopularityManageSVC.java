/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.popularity;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PopularityManageSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset queryPopularityInfo(IData input) throws Exception {
		PopularityManageBean bean = (PopularityManageBean) BeanManager.createBean(PopularityManageBean.class);
		return bean.queryPopularityInfo(input, getPagination());
	}
	
	public void managePopularityInfo(IData input) throws Exception {
		PopularityManageBean bean = (PopularityManageBean) BeanManager.createBean(PopularityManageBean.class);
		bean.managePopularityInfo(input);
	}
	
	public IData queryCode(IData input) throws Exception {
		PopularityManageBean bean = (PopularityManageBean) BeanManager.createBean(PopularityManageBean.class);
		return bean.queryCode(input);
	}
	
	public IDataset queryMaxPriority(IData input) throws Exception {
		PopularityManageBean bean = (PopularityManageBean) BeanManager.createBean(PopularityManageBean.class);
		return bean.queryMaxPriority(input);
	}
}
