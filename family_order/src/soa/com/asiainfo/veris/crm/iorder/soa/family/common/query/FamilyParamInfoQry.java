package com.asiainfo.veris.crm.iorder.soa.family.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;

public class FamilyParamInfoQry {
	/**
	 * 通过成员用户id查询家庭用户关系
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryTransParam(String busiType, String paramCode, String roleCode, String type)
			throws Exception {
		IData param = new DataMap();
		param.put(KeyConstants.BUSI_TYPE, busiType);
		param.put(KeyConstants.PARAM_CODE, paramCode);
		param.put(KeyConstants.ROLE_CODE, roleCode);
		param.put(KeyConstants.ROLE_TYPE, type);
		param.put(KeyConstants.EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
		IDataset result = FamilyDao.qryByCode(FamilySQLEnum.P_SEL_BY_BUSY_TYPE_PARAM_CODE_ROLE, param,
				Route.CONN_CRM_CEN);
		return result;
	}

	public static IDataset queryTransParam(String busiType, String paramCode) throws Exception {
		IData param = new DataMap();
		param.put(KeyConstants.BUSI_TYPE, busiType);
		param.put(KeyConstants.PARAM_CODE, paramCode);
		param.put(KeyConstants.EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
		IDataset result = FamilyDao.qryByCode(FamilySQLEnum.P_SEL_BY_BUSY_TYPE_PARAM_CODE, param, Route.CONN_CRM_CEN);
		return result;
	}

}
