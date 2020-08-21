package com.asiainfo.veris.crm.order.soa.person.busi.queryright;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class QueryCustAndRightsBean extends CSBizBean {

	Logger log = Logger.getLogger(QueryCustAndRightsBean.class);

	public static IDataset queryRightIdByClass(IData inparam) throws Exception {

		return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_RIGHT_BY_CLASS",
				inparam, Route.CONN_CRM_CEN);
	}

	public static IDataset queryRightINamedByClass(IData inparam)
			throws Exception {

		return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL_RIGHTNAME_BY_CLASS",
				inparam, Route.CONN_CRM_CEN);
	}

	public static IDataset queryUserClassBySn(IData inparam) throws Exception {

		return Dao.qryByCodeParser("TF_F_USER_INFO_CLASS",
				"SEL_BY_SN", inparam);
	}

	public static IDataset queryDiscntBySnandID(IData inparam) throws Exception {

		return Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_DATE_BY_SNID",inparam,Route.CONN_CRM_CEN);
	}
	
	
}
