package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;


import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;

import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

/**关于优化“NGBOSS-网厅预约业务处理界面”的需求
 * 获取宽带开户时间，更新网厅预约表
 * @author liwei29
 * @date 2019-12-11
 */
public class DealAcceptDateAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		//String acceptTime = btd.getRD().getAcceptTime();
		
		IData pageData=btd.getRD().getPageRequestData();
		String book_id = pageData.getString("BOOK_ID","");
		String reg_id = btd.getTradeId();
	
		//OPEN_DATE开户时间
		if(StringUtils.isNotEmpty(book_id)){
			MergeWideUserCreateRequestData mergeWideUserCreateRd = ((MergeWideUserCreateRequestData) btd
					.getRD());
			IData cond = new DataMap();
			String openDate = mergeWideUserCreateRd.getOpenDate();
			cond.put("OPEN_DATE", openDate);
			cond.put("SERIAL_NUMBER", mergeWideUserCreateRd
					.getNormalSerialNumber().replaceAll("KD_", ""));
			cond.put("TRADE_ID", book_id);
			cond.put("REG_ID", reg_id);

			StringBuilder sql = new StringBuilder(200);
			sql.append(" UPDATE TF_B_TRADE_BOOK");
			sql.append(" SET RSRV_DATE2 = TO_DATE(:OPEN_DATE, 'YYYY-MM-DD hh24:mi:ss'),RSRV_STR12=:REG_ID,RSRV_STR13=:SERIAL_NUMBER");
			sql.append(" WHERE TRADE_ID = :TRADE_ID AND (RSRV_TAG2<>'1' OR RSRV_TAG2 IS NULL)");
			Dao.executeUpdate(sql, cond, Route.CONN_CRM_CEN);
		}
		
	}
	
}
