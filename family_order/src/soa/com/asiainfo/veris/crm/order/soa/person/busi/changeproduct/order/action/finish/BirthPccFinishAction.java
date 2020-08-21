package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;


/**
 * @Description: 生日权益订购/删除限速处理
 * @author: dengyi5
 * @date: 2019-9-25
 */
public class BirthPccFinishAction implements ITradeFinishAction
{
	
	private static  Logger logger = Logger.getLogger(BirthPccFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception 
	{
		
		if(logger.isDebugEnabled())
		{
			logger.debug("----------BirthPccFinishAction--------in--");
		}
		String tradeId = mainTrade.getString("TRADE_ID");
    	IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----------BirthPccFinishAction--------discntTrades="+discntTrades);
		}
    	if (IDataUtil.isNotEmpty(discntTrades))
    	{
    		String elementId = "";
        	IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2745", "ROAM_BIRTH", CSBizBean.getVisit().getStaffEparchyCode());
        	if (DataUtils.isNotEmpty(commparaSet))
    		{
    			elementId = commparaSet.getData(0).getString("PARA_CODE1");
    		}
        	if(StringUtils.isBlank(elementId))
        	{
        		return;
        	}
    		for(int i=0;i<discntTrades.size();i++)
    		{
    			IData discntTrade = discntTrades.getData(i);
    			String discntCode = discntTrade.getString("DISCNT_CODE");
		    	if(elementId.equals(discntCode))
		    	{
		    		if(logger.isDebugEnabled())
		    		{
		    			logger.debug("----------BirthPccFinishAction--------discntTrade="+discntTrade);
		    		}
		    		IDataset pccSerCpde = CommparaInfoQry.getCommpara("CSM", "2745", "PCC_SER_CODE", CSBizBean.getVisit().getStaffEparchyCode());
		    		
		    		String AcceptDate = SysDateMgr.decodeTimestamp(discntTrade.getString("START_DATE"), "yyyyMMddHHmmss");
		    		String EndDate = SysDateMgr.decodeTimestamp(discntTrade.getString("END_DATE"), "yyyyMMddHHmmss");
		    		String modifyTag = discntTrade.getString("MODIFY_TAG");
		    		
		    		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
		    		{
		    			IData params = new DataMap();
		    	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
		    	        params.put("OPER_CODE", "00");
		    	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
		    	        params.put("PCC_USR_IMSI", "");
		    	        params.put("PCC_USR_STATUS", "1");
		    	        params.put("PCC_USR_BIL_CYC_DATE", "1");
		    	        params.put("TAB_BILL_TYPE", "0");
		    	        params.put("PCC_USR_GRADE", "");
		    	        params.put("PCC_USR_NOTI_MSISDN", "");
		    	        params.put("ROUTETYPE", "00");
		    	        params.put("ROUTEVALUE", "000");
		    	        IDataset results = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", params);
		    	        if(logger.isDebugEnabled())
		    			{
		    				logger.debug("----------BirthPccFinishAction--------results--"+results);
		    			}
		    	        insertLog(params,results,mainTrade.getString("EPARCHY_CODE",""),mainTrade.getString("USER_ID",""),"12");
		    		}else
		    		{
		    			continue;
		    		}
		    		
	    	        
		    		//KEY值不确定，先暂时这样写SERVICE_USAGE_STATE,IBOSS代码没找到
		    		IData serviceBilling = new DataMap();
		    		serviceBilling.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
		    		serviceBilling.put("PCC_SER_CODE", pccSerCpde.getData(0).getString("PARA_CODE1"));
		    		serviceBilling.put("BILL_TYPE", "1");
		    		serviceBilling.put("ACCEPT_DATE", AcceptDate);
		    		serviceBilling.put("END_DATE", EndDate);
		    		serviceBilling.put("SERVICE_USAGE_STATE", "1");//这个不确定，IBOSS那边也不清楚可能需要新加
		    		serviceBilling.put("ROUTETYPE", "00");
		    		serviceBilling.put("ROUTEVALUE", "000");
		    		if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
		    		{//06-订购,07-取消,16-修改
		    			serviceBilling.put("OPER_CODE", "06");
		    			serviceBilling.put("KIND_ID", "BOSS_PCC_PCRF01_0");
		    			IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", serviceBilling);
		    			if(logger.isDebugEnabled())
		    			{
		    				logger.debug("----------BirthPccFinishAction--------resultSet--"+resultSet);
		    			}
		    			dealServicePccParam(mainTrade,resultSet,discntTrade,"A");
		    		}
		    		else
		    		{
		    			continue;
		    		}
    		    }
		    	else
		    	{
		    		continue;
		    	}
    		}
    	}
		
	}

	private void dealServicePccParam(IData mainTrade,IDataset result,IData discntTrade, String operType) throws Exception {
    	String reusltCode="" ,resultInfo = "";
    	if(result.size()>0){
        	IData backData = result.getData(0);
        	if(StringUtils.equals("-1", backData.getString("X_RESULTCODE"))){
        		reusltCode = "9";
        		resultInfo = backData.getString("X_RESULTINFO");
        	}else if(!StringUtils.equals("0000", backData.getString("X_RSPCODE"))){	
        		reusltCode = "9";
        		resultInfo = backData.getString("X_RESDESC", "unknow error");
        	}else{
        		reusltCode = "2";
        		resultInfo = "OK!";
        	}
        }
    	
    	if(logger.isDebugEnabled())
		{
			logger.debug("----------BirthPccFinishAction--------reusltCode--"+reusltCode);
			logger.debug("----------BirthPccFinishAction--------resultInfo--"+resultInfo);
		}
    	IDataset pccSerCpde = CommparaInfoQry.getCommpara("CSM", "2745", "PCC_SER_CODE", CSBizBean.getVisit().getStaffEparchyCode());
    	IData param = new DataMap();
    	String id = SysDateMgr.getSysDateYYYYMMDD() + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
        String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        //限速结束时间
        String userId = mainTrade.getString("USER_ID");
        param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
        param.put("BASS_FLOW_ID", id);
        param.put("ACCEPT_MONTH", month);
        param.put("IN_DATE", SysDateMgr.getSysTime());
        param.put("OPERATION_TYPE", operType);//A签约  U限速 D 删除
        param.put("USER_ID", userId);
        param.put("USR_IDENTIFIER", "86"+mainTrade.getString("SERIAL_NUMBER"));
        param.put("SERVICE_CODE", pccSerCpde.getData(0).getString("PARA_CODE1"));//策略ID
        param.put("SERVICE_START_DATE_TIME", discntTrade.getString("START_DATE"));
        param.put("SERVICE_END_DATE_TIME", discntTrade.getString("END_DATE"));//用户生日当天0点生效，晚上12点失效
        param.put("SERVICE_USAGE_STATE", "1");
        param.put("EXEC_STATE", reusltCode);
        param.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
        param.put("RSRV_STR9", resultInfo);
        param.put("RSRV_STR10", "15");
        param.put("REMARK", "生日权益限速支撑");
        
        Dao.insert("TI_O_PCC_SERVICE", param,Route.CONN_CRM_CEN);
	}
	
	 public void insertLog(IData inData,IDataset result,String eparchyCode,String userId,String userStatus) throws Exception{
	    	
	    	String reusltCode="" ,resultInfo = "";
	    	if(result.size()>0){
	        	IData backData = result.getData(0);
	        	if(StringUtils.equals("-1", backData.getString("X_RESULTCODE"))){
	        		reusltCode = "9";
	        		resultInfo = backData.getString("X_RESULTINFO");
	        	}else if(!StringUtils.equals("0000", backData.getString("X_RSPCODE"))){	
	        		reusltCode = "9";
	        		resultInfo = backData.getString("X_RESDESC", "unknow error");
	        	}else{
	        		reusltCode = "2";
	        		resultInfo = "OK!";
	        	}
	        }
		     String id = SysDateMgr.getSysDateYYYYMMDD()
	                    + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
		     String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
	    	 IData param = new DataMap();
	         param.put("EPARCHY_CODE", eparchyCode);
	         param.put("USR_IDENTIFIER", inData.getString("PCC_USR_IDENT"));
	         param.put("BASS_FLOW_ID", id);
	         param.put("ACCEPT_MONTH", month);
	         param.put("IN_DATE", SysDateMgr.getSysTime());
	         param.put("OPERATION_TYPE", "A");
	         param.put("USER_ID", userId);
	         param.put("USR_STATUS", userStatus);
	         param.put("EXEC_TYPE", "1");//1单个，0批量
	         param.put("EXEC_STATE", reusltCode); 
	         param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
	         param.put("RSRV_STR9", resultInfo); 
	         param.put("RSRV_STR3", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	         Dao.insert("TI_O_PCC_SUBSCRIBER", param, Route.CONN_CRM_CEN);
	    }

}
