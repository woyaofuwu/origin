package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**关于优化“NGBOSS-网厅预约业务处理界面”的需求
 * 获取宽带竣工时间和竣工状态，更新网厅预约表
 * @author liwei29
 * @date 2019-12-11
 */
public class WidenetIsCompleteAciton implements ITradeFinishAction

{

		@Override
		public void executeAction(IData mainTrade) throws Exception {
			
			String serumPhone = mainTrade.getString("SERIAL_NUMBER").replaceAll("KD_", "");
			String RSRV_TAG2 = "1";
			String finish_id = mainTrade.getString("TRADE_ID");
			//String string = SysDateMgr.getSysTime();
			IData cond = new DataMap();
			cond.put("RSRV_TAG2", RSRV_TAG2);
			cond.put("SERIAL_NUMBER", serumPhone);
			cond.put("RSRV_DATE3", SysDateMgr.getSysTime());
			cond.put("FINISH_ID", finish_id);
			StringBuilder sql = new StringBuilder(200);
	    	sql.append(" UPDATE TF_B_TRADE_BOOK");
	    	sql.append(" SET RSRV_TAG2 = :RSRV_TAG2,RSRV_DATE3= TO_DATE(:RSRV_DATE3, 'YYYY-MM-DD hh24:mi:ss')");
	    	sql.append(" WHERE RSRV_STR12 = :FINISH_ID AND (RSRV_TAG2<>'1' OR RSRV_TAG2 IS NULL)");
	    	Dao.executeUpdate(sql, cond,Route.CONN_CRM_CEN);
		        	
		}
}
