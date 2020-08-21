package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


	public class QueryBatTaskPlanQry extends CSBizBean
	{
		
	    public static IDataset queryBatTaskPlan(IData param, Pagination pagination) throws Exception
	    {
	        IData params = new DataMap();
	        params.put("BATCH_TASK_NAME",param.getString("BATCH_TASK_NAME") );
	        params.put("BATCH_OPER_CODE",param.getString("BATCH_OPER_TYPE"));
	        params.put("START_DATE", param.getString("START_DATE"));
	        params.put("END_DATE", param.getString("END_DATE"));
	      
	        return Dao.qryByCode("TF_B_BAT_STAT", "SEL_BAT_STAT_BY_TASKNAME", params, pagination,Route.CONN_CRM_CEN);
	    }
	}


