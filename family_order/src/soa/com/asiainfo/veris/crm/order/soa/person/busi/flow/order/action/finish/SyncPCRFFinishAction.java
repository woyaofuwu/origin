package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;

import java.util.UUID;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;



/**   
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: SyncPCRFFinishAction
 * @Description: REQ201704100004关于BOSS与PCC签约接口开发和测试的需求。
 *
 * @version: v1.0.0
 * @author: zhangxing3   
 * @date: 2017-7-21 下午3:10:25 
 */
public class SyncPCRFFinishAction implements ITradeFinishAction 
{
    Logger log = Logger.getLogger(SyncPCRFFinishAction.class);
    
	@Override
	public void executeAction(IData mainTrade) throws Exception {
	    try{
    	    String tradeId = mainTrade.getString("TRADE_ID","");
    	    String userId = mainTrade.getString("USER_ID","");
    	    String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE", ""); 
    	    String serialNumber = mainTrade.getString("SERIAL_NUMBER", ""); 
    	    String eparchyCode = mainTrade.getString("EPARCHY_CODE","");
    	    boolean is4G = false;
    	    boolean isOpen22 = false;
    	    boolean isStop22 = false;
    	    
    	    if ("10".equals(tradeTypeCode)|| "40".equals(tradeTypeCode) || "310".equals(tradeTypeCode) || "500".equals(tradeTypeCode) || "700".equals(tradeTypeCode))
    	    {
    	    	//通过用户SIM卡资源判断用户是否是4G用户
    	    	IDataset tradeResList =TradeResInfoQry.getTradeRes(tradeId, "1", "0");
    	    	if (log.isDebugEnabled())
    	    		log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeResList:"+tradeResList);
    	    	if (IDataUtil.isNotEmpty(tradeResList)){
    	    		String simCardNo = tradeResList.getData(0).getString("RES_CODE", "");
    	    		if (log.isDebugEnabled())
    	    			log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>simCardNo:"+simCardNo);
    	    		is4G = is4GUser(simCardNo);
    	    	}
    	    	if ( is4G )
    	    	{
	    	    	//用户是4G用户，调用IBOSS接口签约用户属性信息到PCRF
	    	    	IData ibossParam = buildCallIbossParam("KAIHU",mainTrade);	            	
	                IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	             	//这里进行开户日志的保存
	                insertLog(ibossParam,resultSet,mainTrade.getString("EPARCHY_CODE",""),userId,"10");
	            	//判断用户是否开通手机上网服务service_id=22
	            	IDataset tradeSvcList =new DatasetList();
	        	    tradeSvcList = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
	        		if (log.isDebugEnabled())
	        			 log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeSvcList:"+tradeSvcList);
	                if (IDataUtil.isNotEmpty(tradeSvcList)){
	                    for(int i = 0 ; i < tradeSvcList.size() ; i++)
	                    {
	                        IData svcInfo = tradeSvcList.getData(i);
	                        
	                        if ( ("0".equals(svcInfo.getString("MODIFY_TAG",""))&& "22".equals(svcInfo.getString("SERVICE_ID",""))))
	                        {
	                        	isOpen22 = true;
	                        }
	                        
	                    }
	                }
	                //新增的4G且开通了手机上网服务，调用IBOSS接口将用户策略信息同步到PCRF
	                if ( isOpen22 )
	                {
	                	ibossParam = buildCallIbossParam("QIANYUE",mainTrade);
	                	if (log.isDebugEnabled())
	                		log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>ibossParam:"+ibossParam);
	                	//调用IBOSS接口
	                	resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                  //这里进行开户日志策略信息的保存
		                insertLog(ibossParam,resultSet,mainTrade.getString("EPARCHY_CODE",""),userId,"12");
	                }
            	
    	    	}
            	
    	    }
    	    else if ("42".equals(tradeTypeCode) || "41".equals(tradeTypeCode) || "192".equals(tradeTypeCode) || "7240".equals(tradeTypeCode))
    	    {
    	    	
    	    	//通过用户SIM卡资源判断用户是否是4G用户
    	    	//IData ibossParam = new DataMap();
    	    	IDataset tradeResList =TradeResInfoQry.getTradeRes(tradeId, "1", "1");
    	    	if (log.isDebugEnabled())
    	    		log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeResList:"+tradeResList);
    	    	if (IDataUtil.isNotEmpty(tradeResList)){
    	    		String simCardNo = tradeResList.getData(0).getString("RES_CODE", "");
    	    		if (log.isDebugEnabled())
    	    			log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>simCardNo:"+simCardNo);
    	    		is4G = is4GUser(simCardNo);
    	    	}
    	    	if ( is4G )
    	    	{
                
	                //判断用户是否开通手机上网服务service_id=22
	            	IDataset tradeSvcList =new DatasetList();
	        	    tradeSvcList = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
	        		if (log.isDebugEnabled())
	        			 log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeSvcList:"+tradeSvcList);
	                if (IDataUtil.isNotEmpty(tradeSvcList)){
	                    for(int i = 0 ; i < tradeSvcList.size() ; i++)
	                    {
	                        IData svcInfo = tradeSvcList.getData(i);
	                        
	                        if ( ("1".equals(svcInfo.getString("MODIFY_TAG",""))&& "22".equals(svcInfo.getString("SERVICE_ID",""))))
	                        {
	                        	isStop22 = true;
	                        }
	                        
	                    }
	                }
	                //4G用户且关闭手机上网服务，调用IBOSS接口删除用户策略信息同步到PCRF
	                if ( isStop22 )
	                {
	                	//先调用IBOSS接口删除用户策略信息同步到PCRF
	                	IData ibossParam = buildCallIbossParam("JIEYUE",mainTrade);
	                	IDataset backResultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                    //这里进行销户日志策略信息的保存
		                insertLog(ibossParam,backResultSet,eparchyCode,userId,"13");
	                }
	                
	            	//4G用户销户、携出，调用IBOSS接口删除PCRF的用户属性信息
	                IData ibossParam = buildCallIbossParam("XIAOHU",mainTrade);
	                IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	              //这里进行销户日志的保存
	                insertLog(ibossParam,resultSet,eparchyCode,userId,"11");
    	    	}
    	    }
    	    else if ("110".equals(tradeTypeCode) || "120".equals(tradeTypeCode))
    	    {
    	   	    IDataset tradeSvcList =new DatasetList();
        	    tradeSvcList = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        		if (log.isDebugEnabled())
        			 log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeSvcList:"+tradeSvcList);
                if (IDataUtil.isNotEmpty(tradeSvcList)){
                    for(int i = 0 ; i < tradeSvcList.size() ; i++)
                    {
                        IData svcInfo = tradeSvcList.getData(i);

                        if ( ("1".equals(svcInfo.getString("MODIFY_TAG",""))&& "22".equals(svcInfo.getString("SERVICE_ID",""))))
                        {
                        	isStop22 = true;
                        }
                        
                        if ( ("0".equals(svcInfo.getString("MODIFY_TAG",""))&& "22".equals(svcInfo.getString("SERVICE_ID",""))))
                        {
                        	isOpen22 = true;
                        }
                    }
                
	                IDataset userResList = UserResInfoQry.queryUserResByUserIdResType(userId,"1");
	                if (IDataUtil.isNotEmpty(userResList)){
	                	String simCardNo = userResList.getData(0).getString("RES_CODE", "");
	                	is4G = is4GUser(simCardNo);
	                }
	                if ( is4G && isStop22 )
	                {
	                	IData ibossParam = buildCallIbossParam("JIEYUE",mainTrade);
	                	//调用IBOSS接口
	                	IDataset backResultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                  //这里进行销户日志策略信息的保存
		                insertLog(ibossParam,backResultSet,eparchyCode,userId,"13");
	                }
	                if ( is4G && isOpen22 )
	                {
	                	IData ibossParam = buildCallIbossParam("QIANYUE",mainTrade);
	                	//调用IBOSS接口
	                	IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	                	//这里进行开户日志策略信息的保存
		                insertLog(ibossParam,resultSet,mainTrade.getString("EPARCHY_CODE",""),userId,"12");
	                }
                
                }
    	    }
    	    else if ("142".equals(tradeTypeCode))
    	    {
	    	    boolean is4G_old = false;	
    	    	IDataset newResList = TradeResInfoQry.getTradeRes(tradeId, "1", "0");
    	    	IDataset oldResList = TradeResInfoQry.getTradeRes(tradeId, "1", "1");
                if (log.isDebugEnabled())
       			 log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>tradeResList:"+newResList);
                if (IDataUtil.isNotEmpty(newResList) && IDataUtil.isNotEmpty(oldResList) ){
                	String newSimCardNo = newResList.getData(0).getString("RES_CODE", "");
                	String oldSimCardNo = oldResList.getData(0).getString("RES_CODE", "");
                	 if (log.isDebugEnabled())
               			 log.debug(">>>>> 进入 SyncPCRFFinishAction>>>>>newSimCardNo:"+newSimCardNo+",oldSimCardNo:"+oldSimCardNo);
                	 is4G = is4GUser(newSimCardNo);
                	 is4G_old = is4GUser(oldSimCardNo);
                }

                if ( is4G && !is4G_old)
                {
                	//非4G用户换卡为4G SIM，调用IBOSS接口签约用户属性信息到PCRF
                	IData ibossParam = buildCallIbossParam("KAIHU",mainTrade);	            	
	                IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                	
                	IDataset userSvcList = UserSvcInfoQry.getSvcUserId(userId,"22");
	    	    	if (IDataUtil.isNotEmpty(userSvcList)){
	    	    		//如果用户已经开通手机上网功能的，调用IBOSS接口将用户策略信息同步到PCRF
	                	ibossParam = buildCallIbossParam("QIANYUE",mainTrade);
	                    IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
	    	    	}

                }
                else if ( !is4G && is4G_old) 
                {
                	// 4G用户换卡为非4GSIM卡，调用IBOSS接口删除PCRF的用户属性信息
                	IData ibossParam = buildCallIbossParam("XIAOHU",mainTrade);
                    IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
                }

    	    }
    	}catch (Exception e) {
            log.debug("SyncPCRFFinishAction.catch--->"+e.getMessage());
        }
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
	        params.put("SESSIONPOLICY_CODE", "12898050000000000000000000000008");
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
	        params.put("SESSIONPOLICY_CODE", "12898050000000000000000000000008");
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
     * @Description: 是否4G卡用户
     * @param simCardNo
     * @return
     * @throws Exception
     * @author: zhangxing3
     */
    public boolean is4GUser(String simCardNo) throws Exception
    {


        // 调用资源接口
        IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "1");

        if (IDataUtil.isNotEmpty(simCardDatas))
        {
            String simTypeCode = simCardDatas.getData(0).getString("RES_TYPE_CODE", "0").substring(1);// 对应老系统的simtypecode

            IDataset assignParaInfoData = ResParaInfoQry.checkUser4GUsimCard(simTypeCode);

            if (StringUtils.isNotBlank(simTypeCode) && IDataUtil.isNotEmpty(assignParaInfoData))
            {
                return true;
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
            return false;// 因测试资料不全 暂时返回false
        }
       
        return false;
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
