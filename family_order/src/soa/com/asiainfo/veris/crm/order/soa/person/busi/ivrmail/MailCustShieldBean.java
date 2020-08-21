package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MailCustShieldBean extends CSBizBean {
	
	public IDataset exportQueryList(IData param) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select t.SERIAL_NUMBER,to_char(t.CREATE_DATE,'yyyy-mm-dd hh24:mi:ss') as CREATE_DATE,t.CREATE_STAFFID from TD_M_IVRSENDMAILCUST_SHIELD t ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and t.CREATE_DATE >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and t.CREATE_DATE <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		
		IDataset infos = Dao.qryByParse(parser, "ncc");
		
		return infos;
	}
	
	public IDataset queryList(IData param, Pagination page) throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select t.SERIAL_NUMBER,to_char(t.CREATE_DATE,'yyyy-mm-dd hh24:mi:ss') as CREATE_DATE,t.CREATE_STAFFID from TD_M_IVRSENDMAILCUST_SHIELD t ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and t.SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and t.CREATE_DATE >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and t.CREATE_DATE <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		IDataset infos = Dao.qryByParse(parser, page, "ncc");
		return infos;
	}
	
	public boolean addRecord(IData param) throws Exception{
		boolean ret = Dao.insert("TD_M_IVRSENDMAILCUST_SHIELD", param, "ncc");
		return ret;
	}
	
	public int[] addRecordList(IDataset param) throws Exception{
		int ret[] = Dao.insert("TD_M_IVRSENDMAILCUST_SHIELD", param, "ncc");
		return ret;
	}
	
	public boolean delRecord(IData param) throws Exception{
		boolean ret = Dao.delete("TD_M_IVRSENDMAILCUST_SHIELD", param, 
				new String[]{"SERIAL_NUMBER"}, "ncc");
		return ret;
	}
	
	public int[] delRecordList(IDataset param) throws Exception{
		int ret[] = Dao.delete("TD_M_IVRSENDMAILCUST_SHIELD", param, 
				new String[]{"SERIAL_NUMBER"}, "ncc");
		return ret;
	}
	
}
