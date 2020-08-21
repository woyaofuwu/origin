package com.asiainfo.veris.crm.order.soa.person.busi.custspecialuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustSpecialUserBean extends CSBizBean {
	
	public IDataset exportQueryList(IData param) throws Exception{
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select t.SERIAL_NUMBER,t.USER_NAME,t.TYPE,t.FLAG,to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') as START_DATE,to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') as END_DATE,t.RESERVE1,t.RESERVE2,t.RESERVE3,t.RESERVE4,t.RESERVE5,t.RESERVE6,t.RESERVE7,t.RESERVE8,t.RESERVE9,t.RESERVE10,t.REMARK,t.UPDATE_STAFF_ID, to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') as UPDATE_TIME from TF_B_SPECIALUSER t ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and t.USER_NAME = :USER_NAME ");
		parser.addSQL(" and t.TYPE = :TYPE ");
		parser.addSQL(" and t.FLAG = :FLAG ");
		parser.addSQL(" and t.RESERVE1 = :RESERVE1 ");
		parser.addSQL(" and t.RESERVE2 = :RESERVE2 ");
		parser.addSQL(" and t.RESERVE3 = :RESERVE3 ");
		parser.addSQL(" and t.RESERVE4 = :RESERVE4 ");
		parser.addSQL(" and t.RESERVE5 = :RESERVE5 ");
		parser.addSQL(" and t.RESERVE6 = :RESERVE6 ");
		parser.addSQL(" and t.RESERVE7 = :RESERVE7 ");
		parser.addSQL(" and t.RESERVE8 = :RESERVE8 ");
		parser.addSQL(" and t.RESERVE9 = :RESERVE9 ");
		parser.addSQL(" and t.RESERVE10 = :RESERVE10 ");
		parser.addSQL(" and t.REMARK = :REMARK ");
		
		IDataset infos = null;//Dao.qryByParse(parser, Route.CONN_CC);
		
		return infos;
	}

	public IDataset queryList(IData param, Pagination page) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select t.SERIAL_NUMBER,t.USER_NAME,t.TYPE,t.FLAG,to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') as START_DATE,to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') as END_DATE,t.RESERVE1,t.RESERVE2,t.RESERVE3,t.RESERVE4,t.RESERVE5,t.RESERVE6,t.RESERVE7,t.RESERVE8,t.RESERVE9,t.RESERVE10,t.REMARK,t.UPDATE_STAFF_ID, to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') as UPDATE_TIME from TF_B_SPECIALUSER t ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and t.USER_NAME = :USER_NAME ");
		parser.addSQL(" and t.TYPE = :TYPE ");
		parser.addSQL(" and t.FLAG = :FLAG ");
		parser.addSQL(" and t.RESERVE1 = :RESERVE1 ");
		parser.addSQL(" and t.RESERVE2 = :RESERVE2 ");
		parser.addSQL(" and t.RESERVE3 = :RESERVE3 ");
		parser.addSQL(" and t.RESERVE4 = :RESERVE4 ");
		parser.addSQL(" and t.RESERVE5 = :RESERVE5 ");
		parser.addSQL(" and t.RESERVE6 = :RESERVE6 ");
		parser.addSQL(" and t.RESERVE7 = :RESERVE7 ");
		parser.addSQL(" and t.RESERVE8 = :RESERVE8 ");
		parser.addSQL(" and t.RESERVE9 = :RESERVE9 ");
		parser.addSQL(" and t.RESERVE10 = :RESERVE10 ");
		parser.addSQL(" and t.REMARK = :REMARK ");
		
		IDataset infos = null;//Dao.qryByParse(parser, page, Route.CONN_CC);
		
		return infos;
	}
	
	public int[] addRecordList(IDataset param) throws Exception{
		int ret[] = null;//Dao.insert("TF_B_SPECIALUSER", param, Route.CONN_CC);
		return ret;
	}
	
	public boolean addRecord(IData param) throws Exception{
		boolean ret = false;//Dao.insert("TF_B_SPECIALUSER", param, Route.CONN_CC);
		return ret;
	}
	
	public boolean updateRecord(IData param) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		boolean ret = false;//Dao.save("TF_B_SPECIALUSER", param, new String[]{"SERIAL_NUMBER","TYPE"}, Route.CONN_CC);
		
		return ret;
	}
	
	public boolean delRecord(IData param) throws Exception{
		boolean ret = false;//Dao.delete("TF_B_SPECIALUSER", param, new String[]{"SERIAL_NUMBER","TYPE"}, Route.CONN_CC);
		return ret;
	}
	
	public int[] delRecordList(IDataset param) throws Exception{
		int ret[] = null;//Dao.delete("TF_B_SPECIALUSER", param, new String[]{"SERIAL_NUMBER","TYPE"}, Route.CONN_CC);
		return ret;
	}
}
