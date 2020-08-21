package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import java.util.UUID;
import org.apache.log4j.Logger;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

/**
 * @Description 销户删除用户策略
 * @author zhonggb
 * @version 1.0
 */
public class DealPCCInfoToDelAction implements ITradeFinishAction
{
	Logger log = Logger.getLogger(DealPCCInfoToDelAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		if (log.isDebugEnabled())
    		log.debug(">>>>> 进入 PCCDirectionalFUPAction>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		String tradeId = mainTrade.getString("TRADE_ID");
		String userId = mainTrade.getString("USER_ID","");
		String eparchyCode = mainTrade.getString("EPARCHY_CODE");
		IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
		//1.没有订购优惠，不做PCC签约操作
        if (IDataUtil.isEmpty(discntTrades)) {
            return;
        }
        //2.查询是否需要注册参数
        IDataset discntParas = CommparaInfoQry.getCommpara("CSM", "2001","FUP_DIRECTIONAL_PCC", eparchyCode);
        if(IDataUtil.isEmpty(discntParas)) {
        	return;
        }
        for(int i=0,size =discntTrades.size();i<size;i++)
		{
			
			IData discnt = discntTrades.getData(i);
			String discntCode = discnt.getString("DISCNT_CODE");
			
			if(isContainDiscnt(discntParas, discntCode))
			{
				//先调用IBOSS接口删除用户策略信息同步到PCRF
            	IData ibossParam = buildCallIbossParam("JIEYUE",mainTrade);
            	IDataset backResultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                //这里进行销户日志策略信息的保存
                insertLog(ibossParam,backResultSet,eparchyCode,userId,"13");
                
                //调用IBOSS接口删除PCRF的用户属性信息
                IData ibossParam1 = buildCallIbossParam("XIAOHU",mainTrade);
                IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam1);
                //这里进行销户日志的保存
                insertLog(ibossParam,resultSet,eparchyCode,userId,"11");
			}
		}
	}
	
	public boolean isContainDiscnt(IDataset params,String discntCodes)throws Exception
	{
		if(IDataUtil.isNotEmpty(params))
		{
			for(int i=0,size =params.size();i<size;i++)
			{
				IData discnt = params.getData(i);
				String discntCodePara = discnt.getString("PARA_CODE1");
				
				if(StringUtils.equals(discntCodePara, discntCodes))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/***
	 * 构建调用IBOSS的参数
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	public IData buildCallIbossParam(String operType,IData mainTrade) throws Exception
	{
	    IData params = new DataMap();
	    if ("KAIHU".equals(operType))
	    {
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
	        params.put("PCC_USR_NOTI_MSISDN", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	    }
	    else if ("XIAOHU".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "01");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("PCC_USR_IMSI", "");
	        params.put("PCC_USR_STATUS", "1");
	        params.put("PCC_USR_BIL_CYC_DATE", "1");
	        params.put("TAB_BILL_TYPE", "0");
	        params.put("PCC_USR_GRADE", "");
	        params.put("PCC_USR_NOTI_MSISDN", "");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else if ("QIANYUE".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "10");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("SESSIONPOLICY_CODE", "11000010000000000000000000001001");
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", "20501231235959");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else if ("JIEYUE".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "12");
	        params.put("PCC_USR_IDENT", "86"+mainTrade.getString("SERIAL_NUMBER", ""));
	        params.put("SESSIONPOLICY_CODE", "11000010000000000000000000001001");
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    else{
	    	
	    }
	    return params;
	}
	/**
     * @Description: 根据《关于PCC限速及解限速的日志及重处理的优化》对PCC实时接口开户、策略签约的数据进行日志保存--因为后续需要对销户进行错单重新处理，这里将保存调用销户日志
     * @throws Exception
     * @author: chenfeng9
     */
    
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
         //userStatus--1回复速率，2降速--这里新增10为开户，11为销户，12为策略
         param.put("USR_STATUS", userStatus);
         param.put("EXEC_TYPE", "1");//1单个，0批量
         param.put("EXEC_STATE", reusltCode); 
         param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
         param.put("RSRV_STR9", resultInfo); 
         param.put("RSRV_STR3", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
         Dao.insert("TI_O_PCC_SUBSCRIBER", param, Route.CONN_CRM_CEN);
    }
}
