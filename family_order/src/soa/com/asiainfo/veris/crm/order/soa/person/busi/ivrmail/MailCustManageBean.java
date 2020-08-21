package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class MailCustManageBean extends CSBizBean {
	
	public IDataset exportQueryList(IData param) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select t.SERIAL_NUMBER,                                                      ");
		parser.addSQL("        t.SECTION_ID,                                                         ");
		parser.addSQL("        t.MONTH_LIMIT,                                                        ");
		parser.addSQL("        t.SEND_COUNT,                                                         ");
		parser.addSQL("        to_char(t.LAST_SEND_TIME, 'yyyy-mm-dd hh24:mi:ss') as LAST_SEND_TIME, ");
		parser.addSQL("        to_char(t.START_DATE, 'yyyy-mm-dd hh24:mi:ss') as LM_STARTDATE,       ");
		parser.addSQL("        to_char(t.END_DATE, 'yyyy-mm-dd hh24:mi:ss') as LM_ENDDATE            ");
		parser.addSQL("   from TD_M_IVRSENDMAILCUST t                                                ");
		parser.addSQL("  where 1 = 1                                                                 ");
		parser.addSQL("    and t.SERIAL_NUMBER = :SERIAL_NUMBER                                      ");
		parser.addSQL("    and t.SECTION_ID = :SECTION_ID                                            ");
		parser.addSQL("    and t.MONTH_LIMIT = :MONTH_LIMIT                                          ");
		parser.addSQL("    and t.SEND_COUNT = :SEND_COUNT                                            ");
		parser.addSQL("    and t.START_DATE >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')     ");
		parser.addSQL("    and t.END_DATE <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')       ");

		
		IDataset infos = Dao.qryByParse(parser, "ncc");
		
		return infos;
	}
	
	public IDataset queryList(IData param, Pagination page) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select t.SERIAL_NUMBER,                                                      ");
		parser.addSQL("        t.SECTION_ID,                                                         ");
		parser.addSQL("        t.MONTH_LIMIT,                                                        ");
		parser.addSQL("        t.SEND_COUNT,                                                         ");
		parser.addSQL("        to_char(t.LAST_SEND_TIME, 'yyyy-mm-dd hh24:mi:ss') as LAST_SEND_TIME, ");
		parser.addSQL("        to_char(t.START_DATE, 'yyyy-mm-dd hh24:mi:ss') as LM_STARTDATE,       ");
		parser.addSQL("        to_char(t.END_DATE, 'yyyy-mm-dd hh24:mi:ss') as LM_ENDDATE            ");
		parser.addSQL("   from TD_M_IVRSENDMAILCUST t                                                ");
		parser.addSQL("  where 1 = 1                                                                 ");
		parser.addSQL("    and t.SERIAL_NUMBER = :SERIAL_NUMBER                                      ");
		parser.addSQL("    and t.SECTION_ID = :SECTION_ID                                            ");
		parser.addSQL("    and t.MONTH_LIMIT = :MONTH_LIMIT                                          ");
		parser.addSQL("    and t.SEND_COUNT = :SEND_COUNT                                            ");
		parser.addSQL("    and t.START_DATE >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')     ");
		parser.addSQL("    and t.END_DATE <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')       ");

		
		IDataset infos = Dao.qryByParse(parser, page, "ncc");
		
		return infos;
	}
	
	public IDataset querySendLog(IData param, Pagination page) throws Exception{
		IDataset infos = Dao.qryByCodeParser("TL_M_IVRSENDMAIL_LOG", "SEL_NGCC_IVRMAILSENDLOG", param, page, "ncc");
		return infos;
	}
	
	public IDataset exportSendLog(IData param) throws Exception{
		IDataset infos = Dao.qryByCodeParser("TL_M_IVRSENDMAIL_LOG", "SEL_NGCC_IVRMAILSENDLOG", param, "ncc");
		return infos;
	}
	
	
	public int[] addRecordList(IDataset param) throws Exception{
		

		int ret[] = Dao.insert("TD_M_IVRSENDMAILCUST", param, "ncc");
		return ret;
	}
	
	public boolean addRecord(IData param) throws Exception{
		boolean ret = Dao.insert("TD_M_IVRSENDMAILCUST", param, "ncc");
		return ret;
	}
	
	
	public boolean updateRecord(IData param) throws Exception{
		
		SQLParser parser = new SQLParser(param);
		
		//Dao.executeUpdate(parser, routeId)
		boolean ret = Dao.save("TD_M_IVRSENDMAILCUST", param, 
				new String[]{"SERIAL_NUMBER","SECTION_ID"}, "ncc");
		
		return ret;
	}
	
	public boolean delRecord(IData param) throws Exception{
		boolean ret = Dao.delete("TD_M_IVRSENDMAILCUST", param, 
				new String[]{"SERIAL_NUMBER","SECTION_ID"}, "ncc");
		return ret;
	}
	
	public boolean delAllSectionRecord(IData param) throws Exception{
		boolean ret = Dao.delete("TD_M_IVRSENDMAILCUST", param, 
				new String[]{"SERIAL_NUMBER"}, "ncc");
		return ret;
	}
	
	public int[] delRecordList(IDataset param) throws Exception{
		int ret[] = Dao.delete("TD_M_IVRSENDMAILCUST", param, 
				new String[]{"SERIAL_NUMBER","SECTION_ID"}, "ncc");
		return ret;
	}
	
	public int deleteAllRecord(IData param) throws Exception{
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" delete from TD_M_IVRSENDMAILCUST  ");
		return Dao.executeUpdate(parser, "ncc");
	}
	
}
