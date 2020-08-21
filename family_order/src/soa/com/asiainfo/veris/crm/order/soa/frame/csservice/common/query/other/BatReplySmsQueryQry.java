package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class BatReplySmsQueryQry extends CSBizBean{
	 public static IDataset batReplySmsQuery(String startDate, String serialnumber, String replyDate, String replyContent, Pagination page) throws Exception
	    {
		    
		    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		    Date replyDate_new = f.parse(replyDate);
			SimpleDateFormat sdf = new SimpleDateFormat("MM");
			String month = sdf.format(replyDate_new);
			String historyTableName = "UCR_UEC.TF_B_TRADELOG_SMS" + "_" + month;
	        IData params = new DataMap();
	        SQLParser parser = new SQLParser(params);
	        params.put("START_DATE", startDate);
	        params.put("SERIAL_NUMBER", serialnumber);
	        params.put("REPLY_DATE", replyDate);
	        params.put("REPLY_CONTENT", replyContent);
	        parser.addSQL(" SELECT a.*, ");
	        parser.addSQL(" b.REQ_CODE,b.RECEIVE_TIME ");
	        parser.addSQL(" FROM UCR_UEC.TI_OH_SMS_MANUAL a,  ");
	        parser.addSQL( historyTableName + " b WHERE 1=1 ");
            if(startDate != ""){
            	 parser.addSQL(" AND to_date(to_char(a.INSERT_TIME,'yyyy-mm-dd'),'yyyy-MM-dd') = to_date(:START_DATE,'yyyy-MM-dd')  ");
            }
            if(serialnumber != ""){
            	 parser.addSQL(" AND a.SERIAL_NUMBER = :SERIAL_NUMBER ");
            	 parser.addSQL(" AND a.SERIAL_NUMBER = b.SERIAL_NUMBER ");
            }
            if(replyDate != ""){
            	 parser.addSQL(" AND to_date(to_char(b.RECEIVE_TIME,'yyyy-mm-dd'),'yyyy-MM-dd') = to_date(:REPLY_DATE,'yyyy-MM-dd') ");
            }
            if(replyContent != ""){
            	 parser.addSQL(" AND b.REQ_CODE = :REPLY_CONTENT ");
            }
	        //IDataset dataset = Dao.qryByParse(parser);
		    // IDataset dataset = Dao.qryByCode("TI_O_SMS_MANUAL", "SEL_BY_MAN", params, page, Route.CONN_UOP_UEC) ;
    		//  qryByParse(parser)
           IDataset dataset = Dao.qryByParse(parser,page,"uec") ;
	        return dataset;
	    }
}
