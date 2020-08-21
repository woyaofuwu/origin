package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IsspConfigQry extends CSBizBean {

	public static IDataset getParamValue(String configname,String paramName) throws Exception {

		IData ida = new DataMap();
		ida.put("CONFIGNAME", configname);
		ida.put("PARAMNAME", paramName);
		
		SQLParser parser = new SQLParser(ida);

		parser.addSQL(" SELECT t.PARAMVALUE,t.VALUEDESC ");
		parser.addSQL(" FROM td_b_ewe_config t ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND t.PARAMNAME = :PARAMNAME ");
		parser.addSQL(" AND t.CONFIGNAME = :CONFIGNAME");

		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

	}

	public static IDataset getParamValueByDesc(String configname,String paramName,String vlauedesc) throws Exception {

		IData ida = new DataMap();
		ida.put("CONFIGNAME", configname);
		ida.put("PARAMNAME", paramName);
		ida.put("VALUEDESC", vlauedesc);
		
		SQLParser parser = new SQLParser(ida);

		parser.addSQL(" SELECT t.PARAMVALUE ");
		parser.addSQL(" FROM td_b_ewe_config t ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND t.PARAMNAME = :PARAMNAME ");
		parser.addSQL(" AND t.CONFIGNAME = :CONFIGNAME ");
		parser.addSQL(" AND t.VALUEDESC =:VALUEDESC ");

		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

	}
	
	public static IDataset getIsspConfig(String configname) throws Exception {

		IData ida = new DataMap();
		ida.put("CONFIGNAME", configname);
		
		SQLParser parser = new SQLParser(ida);

		parser.addSQL(" SELECT t.PARAMVALUE,t.PARAMNAME ");
		parser.addSQL(" FROM td_b_ewe_config t ");
		parser.addSQL(" WHERE 1=1 ");
		parser.addSQL(" AND t.CONFIGNAME = :CONFIGNAME");
		parser.addSQL(" ORDER BY VALUESEQ");
		return Dao.qryByParse(parser, Route.CONN_CRM_CEN);

	}

}
