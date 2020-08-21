package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ErrorTradeDealSVC extends CSBizService
{
	private static final long serialVersionUID = 5672705627429498087L;
	
	public IData dealErrorTrade(IData params)throws Exception
	{
		IDataset errorTrades = this.qryErrorData();
		if(IDataUtil.isNotEmpty(errorTrades))
		{
			for(int i=0,size =errorTrades.size();i<size;i++)
			{
				IData param = errorTrades.getData(i);
				
				String status = param.getString("STATUS");
				String inParamStr = param.getString("IN_PARAM", "{}");
				IData inParam = new DataMap(inParamStr);
				IData resultData = new DataMap();
				resultData.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
				
				if(!StringUtils.equals("1", status))
	    		{
					String relaTradeId = param.getString("RELA_TRADE_ID");
	    			try{
	    				CSBizBean.getVisit().set("STAFF_ID", param.getString("CREATE_STAFF_ID"));
						CSBizBean.getVisit().set("DEPART_ID", param.getString("CREATE_DEPART_ID"));
						CSBizBean.getVisit().set("IN_MODE_CODE", param.getString("IN_MODE_CODE"));
	    				IDataset result = CSAppCall.call(param.getString("SVC_ID"),inParam);

	            		this.updateExecResult("1", "ok", relaTradeId, result.getData(0).getString("TRADE_ID"));
	    			}
	    			catch(Exception e)
	    			{	                
	    	             //记录失败日志
	    	            String errorMsg = Utility.getBottomException(e).getMessage();
	    	                if(errorMsg != null && !"".equals(errorMsg))
	    	                	if(errorMsg.length() > 2000)
	    	                		errorMsg = errorMsg.substring(0,1999);
	    				resultData.put("X_RESULTCODE", "-1");
	            		resultData.put("X_RESULTINFO", errorMsg);
	            		this.updateExecResult("2", errorMsg, relaTradeId, "");
	    			}
	    		}
			}
		}
    	
		return new DataMap();
	}
	private void updateExecResult(String status,String remark,String relaTradeId,String tradeId) throws Exception
	{
		IData param = new DataMap();
		param.put("STATUS", status);
		param.put("TRADE_ID", tradeId);
		param.put("RELA_TRADE_ID", relaTradeId);
		param.put("REMARK", remark);
		
		StringBuilder sql= new StringBuilder();
		sql.append("UPDATE TF_B_ERROR_TRADE ");
		sql.append("SET STATUS =:STATUS , ");
		if(StringUtils.equals("1", status))
		{
			sql.append(" TRADE_ID =:TRADE_ID ,");
		}
		sql.append(" UPDATE_TIME = sysdate ,");
		sql.append(" REMARK =:REMARK ,");
		sql.append(" DEAL_NUM = DEAL_NUM + 1 ");
		sql.append(" WHERE RELA_TRADE_ID = :RELA_TRADE_ID ");
		
		Dao.executeUpdate(sql, param, Route.getJourDb());
	}
	
	private IDataset qryErrorData() throws Exception
	{
		StringBuilder sql= new StringBuilder();
		sql.append("SELECT RELA_TRADE_ID,ACCEPT_MONTH, RELA_SERIAL_NUMBER, TRADE_ID, SERIAL_NUMBER, SVC_ID, IN_PARAM, STATUS, DEAL_NUM,");
		sql.append(" IN_MODE_CODE, CREATE_TIME, CREATE_STAFF_ID, CREATE_DEPART_ID, UPDATE_TIME, REMARK, RSRV_STR1, RSRV_STR2, ");
		sql.append(" RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, RSRV_DATE1, RSRV_DATE2, RSRV_DATE3 ") ;
		sql.append(" FROM TF_B_ERROR_TRADE WHERE STATUS !='1'");
		
		return Dao.qryBySql(sql, new DataMap(),Route.getJourDb());
	}

}
