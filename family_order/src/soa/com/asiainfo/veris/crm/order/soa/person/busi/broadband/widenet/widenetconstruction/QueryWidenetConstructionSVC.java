package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetconstruction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: QueryWidenetConstructionSVC.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author:
 * @date:
 */

public class QueryWidenetConstructionSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset queryWidenetConstsInfo(IData input) throws Exception {
		QueryWidenetConstructionBean contactLogBean = (QueryWidenetConstructionBean) BeanManager
				.createBean(QueryWidenetConstructionBean.class);
		return contactLogBean.queryWidenetConstsInfo(input, getPagination());
	}

	/**
	 * 
	 * @param input
	 * @throws Exception
	 */
	public void updateConstsPass(IData input) throws Exception {
		QueryWidenetConstructionBean constsBean = BeanManager
				.createBean(QueryWidenetConstructionBean.class);
		constsBean.updateConstsPass(input);
	}

	/**
	 * 
	 * @param input
	 * @throws Exception
	 */
	public void updateConstsNoPass(IData input) throws Exception {
		QueryWidenetConstructionBean constsBean = BeanManager
				.createBean(QueryWidenetConstructionBean.class);
		constsBean.updateConstsNoPass(input);
	}
}
