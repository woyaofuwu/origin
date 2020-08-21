package com.asiainfo.veris.crm.order.soa.group.electronicarchives;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class ElectronicArchivesBean {

	/**
	 ** 根据多个档案编码 获取对应的电子档案信息
	 * @param param
	 * @return
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj 
	 */
	public static IDataset getElectronicArchivesData(IData param) throws Exception {

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT T.ARCHIVES_ID, ");
		parser.addSQL("        T.ARCHIVES_NAME, ");
		parser.addSQL("        T.ARCHIVES_TYPE, ");
		parser.addSQL("        T.SUB_ARCHIVES_TYPE, ");
		parser.addSQL("        T.START_DATE, ");
		parser.addSQL("        T.END_DATE, ");
		parser.addSQL("        T.ARCHIVES_STATE, ");
		parser.addSQL("        T.STATE_DESC, ");
		parser.addSQL("        T.REMARK, ");
		parser.addSQL("        T.ARCHIVES_ATTACH, ");
		parser.addSQL("        T.CREATE_TIME, ");
		parser.addSQL("        T.CREATE_STAFF_ID, ");
		parser.addSQL("        T.CREATE_DEPART_ID, ");
		parser.addSQL("        T.UPDATE_TIME, ");
		parser.addSQL("        T.UPDATE_STAFF_ID, ");
		parser.addSQL("        T.UPDATE_DEPART_ID ");
		parser.addSQL("   FROM TF_F_ELECTRONIC_ARCHIVES T ");
		parser.addSQL("   WHERE T.ARCHIVES_ID IN(" + param.getString("ARCHIVES_IDS") + ") ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}
}
