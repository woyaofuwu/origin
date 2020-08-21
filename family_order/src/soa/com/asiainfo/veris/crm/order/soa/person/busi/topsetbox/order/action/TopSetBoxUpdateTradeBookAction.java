
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.action;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
/**
 * 
 * 宽带开户开魔百和时回传号码至网厅预约处理表
 * REQ202005110032  关于优化线上宽带收单界面的需求
 * @author liwei29
 *
 */
public class TopSetBoxUpdateTradeBookAction implements ITradeFinishAction
{

	@Override
    public void executeAction(IData mainTrade) throws Exception
    {
		String widenitId = mainTrade.getString("RSRV_STR10","");
		
		String serialNumber = mainTrade.getString("SERIAL_NUMBER");//宽带账号
		 //判断是否是宽带开户开的魔百
		 if(StringUtils.isNotEmpty(widenitId)){
			 if(serialNumber.contains("KD_")){
		        	serialNumber=serialNumber.replace("KD_", "");
				}
			    IData cond = new DataMap();
			    cond.put("WIDENIT_ID", widenitId);
				cond.put("BOX_NUM", serialNumber);
				StringBuilder sql = new StringBuilder(200);
		    	sql.append(" UPDATE TF_B_TRADE_BOOK");
		    	sql.append(" SET RSRV_STR14 = :BOX_NUM");
		    	sql.append(" WHERE RSRV_STR12 = :WIDENIT_ID ");
		    	Dao.executeUpdate(sql, cond,Route.CONN_CRM_CEN);
			 
		 }
		
    }
}
