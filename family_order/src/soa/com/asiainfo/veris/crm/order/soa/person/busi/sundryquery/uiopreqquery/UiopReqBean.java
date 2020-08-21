package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.uiopreqquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UiopReqBean extends CSBizBean{

	public IDataset exportQueryList(IData param) throws Exception{
		
		String m = param.getString("START_TIME","").substring(5, 7);
		
		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select * from  ");
		parser.addSQL(" (select MONTH,DAY,TRADE_ID,SVC_CODE,PROTOCOL,IN_MODE_CODE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_EPARCHY_CODE,TRADE_DEPART_PASSWD,TRADE_TERMINAL_ID,BIZ_CODE,TRANS_CODE,SERIAL_NUMBER,to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as START_TIME ,to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as END_TIME ,NEED_TIME,to_char(CALLSTART_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLSTART_TIME ,to_char(CALLEND_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLEND_TIME ,CALL_NEED_TIME,RESULTCODE,RESULTINFO,ISVC_CONTENT,OSVC_CONTENT,REMOTE_IP,LOCAL_IP,PORT ");
		parser.addSQL(" from ucr_uif1.uiop_requestinfo ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and trim(IN_MODE_CODE)=:IN_MODE_CODE ");
		parser.addSQL(" and SERIAL_NUMBER=:SERIAL_NUMBER ");
		parser.addSQL(" and START_TIME >= to_timestamp(:START_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and START_TIME <= to_timestamp(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" union all ");
		parser.addSQL(" select MONTH,DAY,TRADE_ID,SVC_CODE,PROTOCOL,IN_MODE_CODE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_EPARCHY_CODE,TRADE_DEPART_PASSWD,TRADE_TERMINAL_ID,BIZ_CODE,TRANS_CODE,SERIAL_NUMBER,to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as START_TIME ,to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as END_TIME ,NEED_TIME,to_char(CALLSTART_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLSTART_TIME ,to_char(CALLEND_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLEND_TIME ,CALL_NEED_TIME,RESULTCODE,RESULTINFO,ISVC_CONTENT,OSVC_CONTENT,REMOTE_IP,LOCAL_IP,PORT ");
		parser.addSQL(" from ucr_uif1.uiop_requestinfo_" + m + " ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and trim(IN_MODE_CODE)=:IN_MODE_CODE ");
		parser.addSQL(" and SERIAL_NUMBER=:SERIAL_NUMBER ");
		parser.addSQL(" and START_TIME >= to_timestamp(:START_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and START_TIME <= to_timestamp(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" ) order by start_time ");
		
		
		IDataset infos = Dao.qryByParse(parser, Route.CONN_UIF);
		
		return infos;
	}

	public IDataset queryList(IData param, Pagination page) throws Exception{
		
		String m = param.getString("START_TIME","").substring(5, 7);

		SQLParser parser = new SQLParser(param);
		
		parser.addSQL(" select * from  ");
		parser.addSQL(" (select MONTH,DAY,TRADE_ID,SVC_CODE,PROTOCOL,IN_MODE_CODE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_EPARCHY_CODE,TRADE_DEPART_PASSWD,TRADE_TERMINAL_ID,BIZ_CODE,TRANS_CODE,SERIAL_NUMBER,to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as START_TIME ,to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as END_TIME ,NEED_TIME,to_char(CALLSTART_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLSTART_TIME ,to_char(CALLEND_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLEND_TIME ,CALL_NEED_TIME,RESULTCODE,RESULTINFO,ISVC_CONTENT,OSVC_CONTENT,REMOTE_IP,LOCAL_IP,PORT ");
		parser.addSQL(" from ucr_uif1.uiop_requestinfo ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and trim(IN_MODE_CODE) =:IN_MODE_CODE ");
		parser.addSQL(" and SERIAL_NUMBER=:SERIAL_NUMBER ");
		parser.addSQL(" and START_TIME >= to_timestamp(:START_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and START_TIME <= to_timestamp(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" union all ");
		parser.addSQL(" select MONTH,DAY,TRADE_ID,SVC_CODE,PROTOCOL,IN_MODE_CODE,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_EPARCHY_CODE,TRADE_DEPART_PASSWD,TRADE_TERMINAL_ID,BIZ_CODE,TRANS_CODE,SERIAL_NUMBER,to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as START_TIME ,to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as END_TIME ,NEED_TIME,to_char(CALLSTART_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLSTART_TIME ,to_char(CALLEND_TIME,'yyyy-mm-dd hh24:mi:ss.ff6') as CALLEND_TIME ,CALL_NEED_TIME,RESULTCODE,RESULTINFO,ISVC_CONTENT,OSVC_CONTENT,REMOTE_IP,LOCAL_IP,PORT ");
		parser.addSQL(" from ucr_uif1.uiop_requestinfo_" + m + " ");
		parser.addSQL(" where 1=1 ");
		parser.addSQL(" and trim(IN_MODE_CODE) =:IN_MODE_CODE ");
		parser.addSQL(" and SERIAL_NUMBER=:SERIAL_NUMBER ");
		parser.addSQL(" and START_TIME >= to_timestamp(:START_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" and START_TIME <= to_timestamp(:END_TIME,'yyyy-mm-dd hh24:mi:ss') ");
		parser.addSQL(" ) order by start_time ");
		
		IDataset infos = Dao.qryByParse(parser, page, Route.CONN_UIF);
		
		return infos;
	}
}
