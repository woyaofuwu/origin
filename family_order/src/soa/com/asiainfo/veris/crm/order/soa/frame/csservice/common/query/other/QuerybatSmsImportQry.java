package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QuerybatSmsImportQry extends CSBizBean{
	 public static IDataset batSmsImportQuery(String serialNumber, String servicearea, String projectname, String noticecontent, Pagination page) throws Exception
	    {
	        IData params = new DataMap();
	       SQLParser parser = new SQLParser(params);
	        params.put("SERIAL_NUMBER", serialNumber);
	        params.put("SERVICE_AREA", servicearea);
	        params.put("PROJECT_NAME", projectname);
	        params.put("NOTICE_CONTENT", noticecontent);
	        parser.addSQL(" SELECT * ");
	        parser.addSQL(" FROM UCR_UEC.TI_OH_SMS_MANUAL WHERE 1=1 ");
	        if(serialNumber !=""){
	        	parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER ");
	        }
	        if(servicearea !=""){
	        	parser.addSQL(" AND SERVICE_AREA = :SERVICE_AREA ");
	        }
	        if(projectname !=""){
	        	parser.addSQL(" AND PROJECT_NAME = :PROJECT_NAME ");
	        }
	        if(noticecontent !=""){
	        	parser.addSQL(" AND NOTICE_CONTENT = :NOTICE_CONTENT ");
	        }
	        //IDataset dataset = Dao.qryByCode("TI_O_SMS_MANUAL", "SEL_BY_MAN", params, page, Route.CONN_UOP_UEC) ;
	       // IDataset dataset = Dao.qryByCode("TI_O_SMS_MANUAL", "SEL_BY_MAN", params, page, Route.getCrmDefaultDb()) ;
	       //IDataset dataset = Dao.qryByCode("TI_O_SMS_MANUAL", "SEL_BY_MAN", params) ;
	        		//  qryByParse(parser)
	        IDataset dataset = Dao.qryByParse(parser,page,"uec") ;

	        return dataset;
	    }
}
