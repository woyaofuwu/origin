package com.asiainfo.veris.crm.order.soa.group.electronicarchives;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class ElectronicAgreAttachBean {

	/**
	 ** 根据电子协议编码 查询附加电子协议附件信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj
	 */
	public static IDataset getElectronicAgreAttachData(IData param) throws Exception {
		IData params = new DataMap();
		params.put("AGREEMENT_ID", param.getString("AGREEMENT_ID"));

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT T.ARCHIVES_ID, ");
		parser.addSQL("        T.AGREEMENT_ID,  ");
		parser.addSQL("        T.PDF_FILE,  ");
		parser.addSQL("        T.PRODUCT_ID ");
		parser.addSQL("   FROM TF_F_ELECTRONIC_AGRE_ATTACH T ");
		parser.addSQL("  WHERE T.AGREEMENT_ID =:AGREEMENT_ID ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}
}
