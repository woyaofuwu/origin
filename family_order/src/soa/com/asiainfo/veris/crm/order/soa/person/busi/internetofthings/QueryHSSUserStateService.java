package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class QueryHSSUserStateService extends CSBizService{

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 查询HSS方法
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset iBossQuery(IData data)throws Exception{
		
		QueryHSSUserStateBean bean=BeanManager.createBean(QueryHSSUserStateBean.class);
		return bean.iBossQuery(data);
		 
	}
	
}
