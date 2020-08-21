
package com.asiainfo.veris.crm.order.soa.person.busi.pccbusiness;

import java.util.UUID;

import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
 
 

/**
 * pcc业务提供给外部的接口
 */
public class PccActionIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private final static Logger logger = Logger.getLogger(PccActionIntfSVC.class);
    public IData pccSpeedSuspendResume(IData data) throws Exception
    {
        IData returnData = new DataMap(); 
        returnData.put("X_RECORDNUM", "1");
        returnData.put("X_RESULTCODE", "0");
        returnData.put("X_RESULTINFO", "IBOSS操作工单传递成功!"); 
     
        String strParam = data.getString("USER_ID");

        if (StringUtils.isBlank(strParam))
        { 
            returnData.put("X_RESULTCODE", "-1");
            returnData.put("X_RESULTINFO", "接口参数检查: 输入参数[USER_ID]不存在或者参数值为空"); 
            return returnData;
            
        }
        /**
        1：超套餐降速
        2：标准资费用户降速
        3：恢复速率 
        4：流量不限量套餐降速
        5：二级降速
        	国内流量累计达到20GB后，BOSS需要修改usrStatus=2，对达量优速服务用户修改usrStatus为4~6。
			国内流量累计达到100GB后，BOSS需要修改usrStatus=3。
			用户订购流量加油包后，BOSS需要修改usrStatus=1。
        6：一号双终端副设备停机
        7：一号双终端副设备复机
        */
 
        String operation = data.getString("OPERATOR_MIND");

        if (StringUtils.isBlank(operation))
        { 
            returnData.put("X_RESULTCODE", "-1");
            returnData.put("X_RESULTINFO", "接口参数检查: 输入参数[OPERATOR_MIND]不存在或者参数值为空"); 
            return returnData;
        } 
        
        String usrStatus = "";
        
        //处理IBOSS接口参数含义
        if ("1".equals(operation) || "2".equals(operation) || "4".equals(operation))
        {
            usrStatus = "2";

            //按全球通标签提供达量优速服务
            IData roamTagStatus = getRoamTagUserStatus(strParam);
            //按无限流量套餐档位提供达量优速服务
            IData discntUserStatus = getDiscntUserStatus(strParam);
            //两种优速方案并存的情况按就高原则处理
            IData higherSpeedStatus = higherSpeedStatusCompare(roamTagStatus, discntUserStatus);
            if(IDataUtil.isNotEmpty(higherSpeedStatus)) {
                usrStatus = higherSpeedStatus.getString("PARA_CODE2");
            }

        }
        else if ("3".equals(operation))
        {
            usrStatus = "1";
        }else if ("5".equals(operation))
        {
            usrStatus = "3";
        }   

        IDataset userInfos = UserInfoQry.getUserInfoByUserIdTag(data.getString("USER_ID"), "0");
  
        IData userInfo = null;
        if (IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos.getData(0); 
        }
        else
        {  
            returnData.put("X_RESULTCODE", "-1");
            returnData.put("X_RESULTINFO", "用户资料不存在");
            return returnData;
        } 
        //1,2,4降速  3恢复 
        
        data.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        data.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        //checkBusiness(data,returnData);//陈永俏要求去掉所有规则，20170817
        
        if (!"0".equals(returnData.getString("X_RESULTCODE")))
        { 
            return returnData;
        }
        //于下发全球通无限尊享计划限速与国漫FUP限速支撑改造的通知 改造，如果信控传过来的策略ID不为空则表示 是走国漫业务策略ID
        String fupStrategyId = data.getString("FUP_STRATEGY_ID");//信控传送过来的策略ID
        String mdrpStopStrategyId = data.getString("MDRP_STOP_STRATEGY_ID");//一号双终端副设备停复机策略ID
        if(StringUtils.isNotBlank(fupStrategyId)) {
        	//走业务策略，插业务策略表
        	this.dealServicePccProcss(data,userInfo);
        }else if(StringUtils.isNotBlank(mdrpStopStrategyId)){
            if("6".equals(operation)) {
                data.put("OPERATION_TYPE", "A");
            } else if("7".equals(operation)) {
                data.put("OPERATION_TYPE", "D");
            }
            this.dealMdrpStopPccProcess(data, userInfo, mdrpStopStrategyId);
        }else {
        	String id = SysDateMgr.getSysDateYYYYMMDD()
                    + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
	        String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
	        IData param = new DataMap();
	        param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
	        param.put("USR_IDENTIFIER", "86"+userInfo.getString("SERIAL_NUMBER"));
	        param.put("BASS_FLOW_ID", id);
	        param.put("ACCEPT_MONTH", month);
	        param.put("IN_DATE", SysDateMgr.getSysTime());
	        param.put("OPERATION_TYPE", "U");
	        param.put("USER_ID", data.getString("USER_ID"));
	       
	        param.put("USR_STATUS", usrStatus);
	        param.put("EXEC_TYPE", "1");//1单个，0批量
	        param.put("EXEC_STATE", "0"); 
	        param.put("RSRV_STR10", data.getString("START_DATE","")); 
	        param.put("REMARK", data.getString("REMARK")); //解限速备注
	        String userId = data.getString("USER_ID");
	        param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
	        Dao.insert("TI_O_PCC_SUBSCRIBER", param, Route.CONN_CRM_CEN);
        }
        return returnData;
    }
	private void dealServicePccProcss(IData data, IData userInfo) throws Exception {
		String userId = userInfo.getString("USER_ID");
		String eparchyCode = userInfo.getString("EPARCHY_CODE");
		String serialNumber = userInfo.getString("SERIAL_NUMBER");
		String fupStrategyId = data.getString("FUP_STRATEGY_ID");
		IData ibossParam = buildCallIbossParam("KAIHU",fupStrategyId,userInfo);	
		IDataset resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
		//业务策略信息的保存
		dealServicePccParam(resultSet,userId,serialNumber,fupStrategyId,"","15",eparchyCode);//业务策略参数入表
        
		//业务策略签约
		//ibossParam = buildCallIbossParam("SERVICE_QIANYUE",fupStrategyId,userInfo);
		ibossParam = buildCallIbossParam("SERVICE_QIANYUE_NEW",fupStrategyId,userInfo);
    	//调用IBOSS接口
    	resultSet = IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", ibossParam);
        //业务策略信息的保存
		dealServicePccParam(resultSet,userId,serialNumber,fupStrategyId,"","16",eparchyCode);//业务策略参数入表
	}
	private IData buildCallIbossParam(String operType, String fupStrategyId, IData userInfo) throws Exception {
		IData params = new DataMap();
	    if ("KAIHU".equals(operType))
	    {
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "00");
	        params.put("PCC_USR_IDENT", "86"+userInfo.getString("SERIAL_NUMBER", ""));
	        params.put("PCC_USR_IMSI", "");
	        params.put("PCC_USR_STATUS", "1");
	        params.put("PCC_USR_BIL_CYC_DATE", "1");
	        params.put("TAB_BILL_TYPE", "0");
	        params.put("PCC_USR_GRADE", "");
	        params.put("PCC_USR_NOTI_MSISDN", "");
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }else if ("SERVICE_QIANYUE".equals(operType))
	    {
	    	//限速结束时间
	        String serviceEndDate = SysDateMgr.getSysDateYYYYMMDD()+"235959";
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "10");
	        params.put("PCC_USR_IDENT", "86"+userInfo.getString("SERIAL_NUMBER", ""));
	        params.put("SESSIONPOLICY_CODE", fupStrategyId);
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", serviceEndDate);
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }else if ("SERVICE_QIANYUE_NEW".equals(operType))//签业务策略
	    {
	    	//限速结束时间
	        String serviceEndDate = SysDateMgr.getSysDateYYYYMMDD()+"235959";
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "06");
	        params.put("PCC_USR_IDENT", "86"+userInfo.getString("SERIAL_NUMBER", ""));
	        params.put("PCC_SER_CODE", fupStrategyId);
	        //params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", serviceEndDate);
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	    }
	    return params;
	}
	private void checkBusiness(IData data,IData returnData) throws Exception
    {
   
        checkSvc(data, returnData);
        
        if (!"0".equals(returnData.getString("X_RESULTCODE")))
        { 
            return;
        }
        
		/* BUG20170726115759任我用套餐用户信控限速失败解决方案 :在信控调用CRM接口
		 * SS.PccActionSVC.pccIntf时，crm接口内不查询ucr_cen1.Ti_o_Pcc_Subscriber表数据。
		 * edit by zhangxing3 on 2017/8/1
		*/
        //checkSubscriber(data, returnData);
        
        if (!"0".equals(returnData.getString("X_RESULTCODE")))
        { 
            return;
        }
        
        checkPolocy(data, returnData);
    }

    private void checkSvc(IData data, IData returnData) throws Exception
    {
        String operation = data.getString("OPERATOR_MIND"); 
        
        if ("1".equals(operation) || "2".equals(operation) || "4".equals(operation))
        {
            IDataset paramResult = StaticUtil.getStaticList("PCC_DICSERVICE_ID"); 

            if (null != paramResult && !paramResult.isEmpty())
            {
                String serviceID = paramResult.getData(0).getString("DATAID");

                if (StringUtils.isNotEmpty(serviceID))
                { 

                    IDataset dataset = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(data.getString("USER_ID"), serviceID); 
                    
                    if (!dataset.isEmpty())
                    {
                        String strError = "业务检查: 此用户订购了免限速服务,不允许限速";

                        returnData.put("X_RESULTCODE", "-1");
                        returnData.put("X_RESULTINFO", strError); 
                        return;
                    }

                }
            }
        }else{
        	
        	 IData   inParamNew = new DataMap();
        	 inParamNew.put("USER_ID", data.getString("USER_ID"));
             IDataset result =  PCCBusinessQry.qryPccOperationTypeForSubscriber(inParamNew);
             if (!result.isEmpty())
             {
            	 String usrStatus = result.getData(0).getString("USR_STATUS","");
            	 String execState = result.getData(0).getString("EXEC_STATE","");
                 if(!"2".equals(usrStatus)&&"2".equals(execState)){
                     String strError = "业务检查: 此用户不是限速状态,不允许做恢复";

                     returnData.put("X_RESULTCODE", "-1");
                     returnData.put("X_RESULTINFO", strError); 
                     return;
                	 
                 }
             }
             
        }
    }
    private void checkPolocy(IData data,IData resultInfo) throws Exception
    {
        if (!( "1".equals(data.getString("OPERATOR_MIND")) || "2".equals(data.getString("OPERATOR_MIND")) || "4".equals(data.getString("OPERATOR_MIND"))))
        {
            //限速才需要校验
           return; 
        }
        IData inParam = new DataMap();
        inParam.put("USER_ID", data.getString("USER_ID"));
        inParam.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        inParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        
        //1：超套餐降速
        //2：标准资费用户降速
        //4：流量不限量套餐降速
        if ("1".equals(data.get("OPERATOR_MIND")))
        {
            inParam.put("USR_SESSION_POLICY_CODE","11000030000000000000000000000004");
        }else if ("2".equals(data.get("OPERATOR_MIND"))) {
        	inParam.put("USR_SESSION_POLICY_CODE","11000030000000000000000000000005");
		}else if ("4".equals(data.get("OPERATOR_MIND"))) {
			inParam.put("USR_SESSION_POLICY_CODE","12898010000000000000000000000013");
		}
        
        IDataset result = PCCBusinessQry.qryPccOperationTypeForPolocy(inParam);

        if ((null == result || result.isEmpty())
           || "D".equals(result.getData(0).getString("OPREATION_TYPE")))
        {
            checkUser(inParam,resultInfo);
        }
 
    }
    
    private void checkUser(IData inParam,IData resultInfo) throws Exception
    {    
        if (!PCCBusinessQry.is4GUser(inParam.getString("USER_ID")))
        {
            String strError = "业务检查: 此用户不是4G用户"; 
            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
            return;
        }
       String sessionCode = inParam.getString("USR_SESSION_POLICY_CODE");
       
       boolean isGPRSuser = PCCBusinessQry.isGPRSUser(inParam.getString("USER_ID"));
       
        //套餐用户限速
        if ("11000030000000000000000000000004".equals(sessionCode) && !isGPRSuser)
        {
            String strError = "业务检查: 此用户没有订购流量可选包"; 
            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
            return;
        }
        
        //标准用户限速
        if ("11000030000000000000000000000005".equals(sessionCode) && isGPRSuser)
        {
            String strError = "业务检查: 此用户订购了流量可选包"; 
            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
            return;
        }
        
       //流量不限量套餐限速
        if ("12898010000000000000000000000013".equals(sessionCode) && isGPRSuser)
        {
            String strError = "业务检查: 此用户订购了流量可选包"; 
            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
            return;
        }
        
        
        String id = SysDateMgr.getSysDateYYYYMMDD()
                    + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);

        String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        IData param = new DataMap();
        param.put("EPARCHY_CODE", inParam.getString("EPARCHY_CODE"));
        param.put("USR_IDENTIFIER", inParam.getString("SERIAL_NUMBER"));
        param.put("BASS_FLOW_ID", id);
        param.put("ACCEPT_MONTH", month);
        param.put("IN_DATE", SysDateMgr.getSysTime());
        param.put("OPERATION_TYPE", "A");
        param.put("USER_ID", inParam.getString("USER_ID"));

        param.put("USR_SESSION_POLICY_CODE", inParam.getString("USR_SESSION_POLICY_CODE"));
        param.put("SESSION_POLICY_START_DATE_TIME", SysDateMgr.getSysTime()); 
        param.put("SESSION_POLICY_END_DATE_TIME", SysDateMgr.getTheLastTime());
        param.put("EXEC_TYPE", "1");
        param.put("EXEC_STATE", "0");  
        String userId = inParam.getString("USER_ID");
        param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
        
        Dao.insert("TI_O_PCC_POLOCY", param, Route.CONN_CRM_CEN);
        
      
    }
    private void checkSubscriber(IData processInfo, IData resultInfo) throws Exception
    {
        IData   inParam = new DataMap();
        inParam.put("USER_ID", processInfo.get("USER_ID"));
        IDataset result =  PCCBusinessQry.qryPccOperationTypeForSubscriber(inParam);

        if (null == result || result.isEmpty())
        {
            String strError = "业务检查: 此用户没有生效的签约记录";

            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
            return;
        }

        IData dataItem = result.getData(0);
        String operationType = dataItem.getString("OPREATION_TYPE");
        if ("D".equals(operationType))
        {
            String strError = "业务检查: 此用户没有生效的签约记录";

            resultInfo.put("X_RESULTCODE", "-1");
            resultInfo.put("X_RESULTINFO", strError);
        }
    }
    
    public IDataset pccopensyncIntf(IData param) throws Exception{
   	 	IData data=new DataMap();
   	 	String serialNum=param.getString("SERIAL_NUMBER","");
   	    String sessionpolicyCode=param.getString("SESSIONPOLICY_CODE","");
   	    logger.debug("param======"+param);
   	    
   	 	if("0".equals(param.getString("PCC_CODE","")))
   	 	{
	   	 	IData params = new DataMap();
		   	IData map = new DataMap();
			params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
			params.put("OPER_CODE", "00");
			params.put("PCC_USR_IDENT", "86"+serialNum);
			params.put("PCC_USR_IMSI", "");
			params.put("PCC_USR_STATUS", "1");
			params.put("PCC_USR_BIL_CYC_DATE", "1");
			params.put("TAB_BILL_TYPE", "0");
			params.put("PCC_USR_GRADE", "");
			params.put("PCC_USR_NOTI_MSISDN", "");
			params.put("ROUTETYPE", "00");
			params.put("ROUTEVALUE", "000");
			
			IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", params);
			
			map.put("KIND_ID", "BOSS_PCC_PCRF01_0");
			map.put("OPER_CODE", "10");
			map.put("PCC_USR_IDENT", "86"+serialNum);
			map.put("SESSIONPOLICY_CODE", sessionpolicyCode);
			map.put("NOTIFICATION_CYCLE", "");
			map.put("TERMINAL_TYPE", "");
			map.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			map.put("END_DATE", "20501231235959");
			map.put("ROUTETYPE", "00");
	        map.put("ROUTEVALUE", "000");
	        
	        return IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", map);
   	 	}else if ("1".equals(param.getString("PCC_CODE",""))) {
			IData params = new DataMap();
		   	IData map = new DataMap();
			params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
			params.put("OPER_CODE", "02");
			params.put("PCC_USR_IDENT", "86"+serialNum);
			params.put("PCC_USR_IMSI", "");
			params.put("PCC_USR_STATUS", "1");
			params.put("PCC_USR_BIL_CYC_DATE", "1");
			params.put("TAB_BILL_TYPE", "0");
			params.put("PCC_USR_GRADE", "");
			params.put("PCC_USR_NOTI_MSISDN", "");
			params.put("ROUTETYPE", "00");
			params.put("ROUTEVALUE", "000");
			
			return IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", params);
   	 	}else
   	 	{
	   	 	IData params = new DataMap();
		   	IData map = new DataMap();
	        params.put("KIND_ID", "BOSS_PCC_PCRF01_0");
	        params.put("OPER_CODE", "12");
	        params.put("PCC_USR_IDENT", "86"+serialNum);
	        params.put("SESSIONPOLICY_CODE", sessionpolicyCode);
	        params.put("NOTIFICATION_CYCLE", "");
	        params.put("TERMINAL_TYPE", "");
	        params.put("START_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	        params.put("ROUTETYPE", "00");
	        params.put("ROUTEVALUE", "000");
	        
	        return IBossCall.dealInvokeUrl("BOSS_PCC_PCRF01_0", "IBOSS8", params);
   	 	}
   }
    private void dealServicePccParam(IDataset result, String userId,String serialNumber,String serviceCode,String serviceUsageState,String state,String eparchyCode) throws Exception {
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
    	IData param = new DataMap();
    	String id = SysDateMgr.getSysDateYYYYMMDD() + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
        String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        //限速结束时间
        String serviceEndDate = SysDateMgr.getSysDate()+SysDateMgr.END_DATE;
        param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
        param.put("BASS_FLOW_ID", id);
        param.put("ACCEPT_MONTH", month);
        param.put("IN_DATE", SysDateMgr.getSysTime());
        param.put("OPERATION_TYPE", "A");//A签约  U限速 D 删除
        param.put("USER_ID", userId);
        param.put("USR_IDENTIFIER", "86"+serialNumber);
        param.put("SERVICE_CODE", serviceCode);//策略ID
        param.put("SERVICE_START_DATE_TIME", SysDateMgr.getSysTime());
        param.put("SERVICE_END_DATE_TIME", serviceEndDate);//国际漫游方向的业务策略每天零点自动失效
        param.put("SERVICE_USAGE_STATE", serviceUsageState);
        param.put("EXEC_STATE", reusltCode);
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("RSRV_STR9", resultInfo);
        //state--15 业务注册，16 业务策略注册
        param.put("RSRV_STR10", state);
        param.put("REMARK", "无限尊享计划限速与国漫FUP限速支撑");
        
        this.dealFUPInfo("TI_O_PCC_SERVICE", param);
	}
	private void dealFUPInfo(String tableName,IData param) throws Exception {
		Dao.insert(tableName, param,Route.CONN_CRM_CEN);
	}
	private void dealMdrpStopPccProcess(IData data, IData userInfo, String serviceCode) throws Exception {
        String userId = data.getString("USER_ID");
        String id = SysDateMgr.getSysDateYYYYMMDD()
                + UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 20);
        String month = SysDateMgr.getTheMonth(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
        IData param = new DataMap();
        param.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
        param.put("BASS_FLOW_ID", id);
        param.put("ACCEPT_MONTH", month);
        param.put("IN_DATE", SysDateMgr.getSysTime());
        param.put("OPERATION_TYPE", data.getString("OPERATION_TYPE"));//A新增记录 U修改记录 D 删除记录
        param.put("USER_ID", userId);
        param.put("USR_IDENTIFIER", "86"+userInfo.getString("SERIAL_NUMBER"));
        param.put("SERVICE_CODE", serviceCode);//策略ID
        param.put("SERVICE_START_DATE_TIME", SysDateMgr.getSysTime());
        param.put("SERVICE_END_DATE_TIME", SysDateMgr.END_DATE_FOREVER);
        param.put("EXEC_STATE", "0");
        param.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        param.put("REMARK", "一号双终端副设备停复机");

        this.dealFUPInfo("TI_O_PCC_SERVICE", param);
    }

    /**
     * 查询达量优速速率接口（SS.PccActionSVC.queryLimitSpeed）
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryLimitSpeed(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");

        IData roamTagStatus = getRoamTagUserStatus(userId);
        IData discntUserStatus = getDiscntUserStatus(userId);
        IData higherSpeedStatus = higherSpeedStatusCompare(roamTagStatus, discntUserStatus);

        IDataset resultSet = new DatasetList();
        IData resultData = new DataMap();
        if(IDataUtil.isEmpty(higherSpeedStatus)) {
            return null;
        } else {
            resultData.put("SPEED_RATIO", higherSpeedStatus.getString("PARA_CODE3"));
            resultData.put("SPEED_REMARK", higherSpeedStatus.getString("PARA_CODE4"));
        }
        resultSet.add(resultData);
        return resultSet;
    }

    /**
     * 根据全球通标签等级获取对应限速状态
     * @param user_id
     * @return
     * @throws Exception
     */
    private IData getRoamTagUserStatus(String user_id) throws Exception {
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(user_id);
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        IData roamTagParam = new DataMap();
        roamTagParam.put("SERIAL_NUMBER", serialNumber);
        IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", roamTagParam);
        if(DataSetUtils.isNotBlank(roamTag)) {
            String gsmState = roamTag.getData(0).getString("USER_CLASS");//1 银卡 2 金卡 3 白金卡 4 钻卡 5 终身钻卡
            if(StringUtils.isNotBlank(gsmState)) {
                IDataset commparaSet = CommparaInfoQry.getCommparaByCode1("CSM", "2001", "PCC_IMPROVED_SPEED", gsmState, null);
                if(DataSetUtils.isNotBlank(commparaSet)) {
                    return commparaSet.first();//根据标签等级修改限速状态
                }
            }
        }
        return null;
    }

    /**
     * 根据不限流量套餐档位获取对应限速状态
     * @param userId
     * @return
     * @throws Exception
     */
    private IData getDiscntUserStatus(String userId) throws Exception {
        IDataset userDiscntSet = UserDiscntInfoQry.getAllDiscntInfo(userId);
        IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2001", "PCC_IMPROVED_SPEED_DISCNT", null);
        if(DataSetUtils.isBlank(commparaSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_902);
        }
        IData improvedSpeedDiscntMap = new DataMap();
        for (int i = 0; i < commparaSet.size(); i++) {
            String ImprovedSpeedDiscntId = commparaSet.getData(i).getString("PARA_CODE1");
            improvedSpeedDiscntMap.put(ImprovedSpeedDiscntId, i);
        }
        for (int i = 0; i < userDiscntSet.size(); i++) {
            IData userDiscntData = userDiscntSet.getData(i);
            String discntCode = userDiscntData.getString("DISCNT_CODE");
            if(improvedSpeedDiscntMap.containsKey(discntCode)) {//用户资费中有无限流量套餐对应的资费编码时修改限速状态，实现达量优速
                return commparaSet.getData(improvedSpeedDiscntMap.getInt(discntCode));
            }
        }
        return null;
    }

    /**
     * 按就高原则选取更高速率的限速状态
     * @param data1
     * @param data2
     * @return
     * @throws Exception
     */
    private IData higherSpeedStatusCompare(IData data1, IData data2) throws Exception {
        int speed1, speed2;

        if(IDataUtil.isEmpty(data1)) {
            speed1 = 0;
        } else {
            speed1 = data1.getInt("PARA_CODE3");
        }
        if(IDataUtil.isEmpty(data2)) {
            speed2 = 0;
        } else {
            speed2 = data2.getInt("PARA_CODE3");
        }

        return speed1 > speed2 ? data1 : data2;
    }
}
