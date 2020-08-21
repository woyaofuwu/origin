package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querybattaskplan;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryBatTaskPlanQry;

public class QueryBatTaskPlanBean extends CSBizBean {
	
	/**
	 * 功能：批量业务计划查询 author@yanmm
	 */
	public IDataset queryBatTaskPlan(IData data, Pagination page) throws Exception 
	{
		// String tradeEparchyCode = this.getTradeEparchyCode();//
		// BizRoute.getRouteId();
		//String batch_task_name = data.getString("BATCH_TASK_NAME", "");
		//String batch_oper_name = data.getString("BATCH_OPER_NAME", "");
		//String start_date = data.getString("START_DATE", "");
		IDataset dataSet = QueryBatTaskPlanQry.queryBatTaskPlan(data, page);return dataSet;
		}
	}
