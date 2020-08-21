package com.asiainfo.veris.crm.order.soa.group.electronicarchives;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class ElectronicAgreementBean {

	/**
	 ** 根据电子协议编码 查询正文 电子协议信息
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @Date 2019年10月23日
	 * @author xieqj
	 */
	public static IDataset getElectronicAgreementData(IData param) throws Exception {
		IData params = new DataMap();
		params.put("AGREEMENT_ID", param.getString("AGREEMENT_ID"));

		SQLParser parser = new SQLParser(param);
		parser.addSQL(" SELECT T.ARCHIVES_ID, ");
		parser.addSQL("        T.AGREEMENT_ID, ");
		parser.addSQL("        T.A_NAME, ");
		parser.addSQL("        T.A_ADDRESS, ");
		parser.addSQL("        T.A_HEADER, ");
		parser.addSQL("        T.A_CONTACT_PHONE, ");
		parser.addSQL("        T.A_BANK, ");
		parser.addSQL("        T.A_BANK_ACCT_NO, ");
		parser.addSQL("        T.A_SIGN_MAN, ");
		parser.addSQL("        T.A_SIGN_DATE, ");
		parser.addSQL("        T.B_NAME, ");
		parser.addSQL("        T.B_ADDRESS, ");
		parser.addSQL("        T.B_HEADER, ");
		parser.addSQL("        T.B_CONTACT_PHONE, ");
		parser.addSQL("        T.B_BANK, ");
		parser.addSQL("        T.B_BANK_ACCT_NO, ");
		parser.addSQL("        T.B_SIGN_DATE, ");
		parser.addSQL("        T.B_SIGN_MAN, ");
		parser.addSQL("        T.PDF_FILE, ");
		parser.addSQL("        T.PRODUCT_ID, ");
		parser.addSQL("        T.CONTRACT_CODE ");
		parser.addSQL("   FROM TF_F_ELECTRONIC_AGREEMENT T ");
		parser.addSQL("  WHERE T.AGREEMENT_ID =:AGREEMENT_ID ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CG);
	}

}
