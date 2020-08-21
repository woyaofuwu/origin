package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querybattaskplan;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unfinishtrade.QueryUnfinishTradeBean;

public class QueryBatTaskPlanSVC extends CSBizService {
	private static final long serialVersionUID = 4829236996987004886L;

	/**
	 * 功能：批量任务计划查询 author@yanmm
	 */
	public IDataset queryBatTaskPlan(IData data) throws Exception {
		QueryBatTaskPlanBean bean = (QueryBatTaskPlanBean) BeanManager
				.createBean(QueryBatTaskPlanBean.class);
		return bean.queryBatTaskPlan(data, getPagination());
	}
}
