package com.asiainfo.veris.crm.iorder.soa.family.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class FamilyUserInfoQry {
	/**
	 * 通过管理员USER_ID查询家庭用户资料
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IData qryFamilyUserInfoByManagerUserId(String userId) throws Exception {

		IData param = new DataMap();
		param.put(KeyConstants.MEMBER_USER_ID, userId);
		IDataset familyChas = FamilyDao.qryByCode(FamilySQLEnum.FAMILY_USER_INFO_BY_MANAGER_USER_ID, param);
		if (IDataUtil.isNotEmpty(familyChas)) {
			IData cha = familyChas.getData(0);
			String familyUserId = cha.getString(KeyConstants.FAMILY_USER_ID);
			return UcaInfoQry.qryUserInfoByUserId(familyUserId);
		}
		return null;
	}
}
