package com.asiainfo.veris.crm.iorder.soa.family.common.dao;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;

public class FamilyDao {
	public final static IDataset qryByCode(FamilySQLEnum sql, IData param) throws Exception {
		return Dao.qryByCode(sql.getTabName(), sql.getSqlRef(), param);
	}

	public final static IDataset qryByCode(FamilySQLEnum sql, IData param, Pagination page) throws Exception {
		return Dao.qryByCode(sql.getTabName(), sql.getSqlRef(), param, page);
	}

	public final static IDataset qryByCode(FamilySQLEnum sql, IData param, Pagination page, String routeId)
			throws Exception {
		return Dao.qryByCode(sql.getTabName(), sql.getSqlRef(), param, page, routeId);

	}

	public final static IDataset qryByCode(FamilySQLEnum sql, IData param, String routeId) throws Exception {
		return Dao.qryByCode(sql.getTabName(), sql.getSqlRef(), param, routeId);
	}
}
