package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserGiveClassInfoQry {

	public static IDataset queryUserGiveClassDetailByFromtradeid(IData input) throws Exception {
		return Dao.qryByCode("TL_B_USER_GIVE_CLASS_DETAIL", "SEL_BY_FROMTRADE_ID", input);
	}
	
	public static IDataset queryUserGiveClassDetailByFromtradeidGiveSn(IData input) throws Exception {
		return Dao.qryByCode("TL_B_USER_GIVE_CLASS_DETAIL", "SEL_BY_FROMTRADEID_GIVESERIALNUMBER", input);
	}

	public static IDataset queryUserClassByUseridSn(IData input) throws Exception {

		IDataset dataset = Dao.qryByCode("TL_B_USER_GIVE_CLASS", "SEL_BY_USERID_SN", input);
		if (IDataUtil.isNotEmpty(dataset) && dataset.size() > 0) {
			for (int i = 0; i < dataset.size(); i++) {
				IData data = dataset.getData(0);
				String userClassName = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
				data.put("USER_CLASS_NAME", userClassName);
			}
			return dataset;
		} else {
			return null;
		}

	}

	public static IDataset queryUserClassDetailBySn(String serialNumber) throws Exception {
		IData cond = new DataMap();
		cond.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TL_B_USER_GIVE_CLASS_DETAIL", "SEL_BY_SERIALNUMBER", cond);
	}
	
	public static IDataset queryUserClassDetailByGiveSn(String giveSerialNumber) throws Exception {
		IData cond = new DataMap();
		cond.put("GIVE_SERIAL_NUMBER", giveSerialNumber);
		return Dao.qryByCode("TL_B_USER_GIVE_CLASS_DETAIL", "SEL_BY_GIVESERIALNUMBER", cond);
	}

	public static IDataset queryUserClassBySn(String serialNumber) throws Exception {
		
		IData cond = new DataMap();
		cond.put("SERIAL_NUMBER", serialNumber);
		
		IDataset dataset = Dao.qryByCode("TL_B_USER_GIVE_CLASS", "SEL_BY_SN", cond);
		if (IDataUtil.isNotEmpty(dataset) && dataset.size() > 0) {
			for (int i = 0; i < dataset.size(); i++) {
				IData data = dataset.getData(0);
				String userClassName = StaticUtil.getStaticValue("USER_INFO_CLASS", data.getString("USER_CLASS"));
				data.put("USER_CLASS_NAME", userClassName);
			}
			return dataset;
		} else {
			return null;
		}
	}

	public static IDataset queryUserClassByVaildDate(IData input) throws Exception {
		return Dao.qryByCode("TL_B_USER_GIVE_CLASS", "SEL_BY_USER_ID_VAILD_DATE", input);
	}
}
