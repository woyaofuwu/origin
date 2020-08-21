package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.action.finish;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 无手机宽带拆机成功后释放宽带账号。
 * @author chenxy3
 */
public class NoPhoneWideReturnSN implements ITradeFinishAction {

	@Override
	public void  executeAction(IData mainTrade) throws Exception 
	{ 
		String serialNumber = mainTrade.getString("SERIAL_NUMBER",""); 
		String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE","");
		if(tradeTypeCode!=null &&("685".equals(tradeTypeCode) || "687".equals(tradeTypeCode))){
			String remark="";
			if("685".equals(tradeTypeCode)){
				remark="无手机宽带拆机释放账号";
			}else if("687".equals(tradeTypeCode)){
				remark="无手机宽带特殊拆机释放账号";
			}
			//成功，更新OTHER表状态
			String accountId=serialNumber.replace("KD_", "");
	    	IData inparam = new DataMap();
	    	inparam.put("ACCOUNT_ID", accountId); 
	    	inparam.put("REMARK", remark);
	    	StringBuilder sql = new StringBuilder(1000);
	    	
	    	sql.append(" update td_b_widenet_account t ");
	    	sql.append(" set t.state='0',t.occupation_time=null ,T.UPDATE_TIME=SYSDATE,T.REMARK=:REMARK");
	    	sql.append("  WHERE t.account_id=:ACCOUNT_ID "); 
	    	sql.append("  AND sysdate < end_date	");
	        Dao.executeUpdate(sql, inparam,Route.CONN_CRM_CEN) ; 
		}
	}
}
