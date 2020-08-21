package com.asiainfo.veris.crm.order.soa.person.busi.resale;


import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.InterforResalQry;


/**
 * @author yiyb
 * @create_date：2014-07
 */
public class InterForResaleIntfBean extends CSBizBean 
{
	private static final Logger logger = Logger.getLogger(InterForResaleIntfBean.class);
	public IData dealSvcOrder(IData input) throws Exception
	{
		IData returnData = new DataMap();
		IDataset dealList = new DatasetList();
		String strTradeId = "0";
		String strOrderId = "0";
		boolean bIsDup = false; 
		input.put("UPDATE_TIME", SysDateMgr.getSysTime()); 
		
		String rspCode = "0000";
		String rspDesc = "服务开通成功！";
		String oprCode = input.getString("OPR_CODE");
		
		IDataset datasetResult = queryOrderPF(input);
		if(IDataUtil.isNotEmpty(datasetResult))
		{
			//如果取到的状态是报竣成功的状态，修改状态重新报竣，如果为其他状态则直接返回，由上一笔订单进行报竣处理
			String status = datasetResult.getData(0).getString("STATUS");
			bIsDup = true;
			if ("3".equals(status))  
			{
				rspCode = "0000";
				rspDesc = "报竣成功1";
				IData inparams=new DataMap();
				inparams.put("STATE", "3");
				inparams.put("RSPCODE", rspCode);
				inparams.put("RSPDESC", rspDesc);
				inparams.put("SERIAL_NUMBER", datasetResult.getData(0).getString("MSISDN"));
				inparams.put("OPR_NUMB", datasetResult.getData(0).getString("OPR_NUMB"));
				InterForResaleIntfBean.updTradeLTEB(inparams);
				returnData.put("OPR_NUMB", input.getString("OPR_NUMB"));
				returnData.put("RSP_CODE",  rspCode);
				returnData.put("RSP_DESC", rspDesc);
				return returnData; 
			}
		}
		else
		{ 
			InterforResalQry.saveOrderInfo(input); 
		}
		try 
		{
			// 根据号码获取路由的方法，需要公用出来，最好放专门的路由类，或放基类里，直接调用
	        String strEparchyCode = RouteInfoQry.getEparchyCodeBySn(input.getString("MSISDN"));
	        if (StringUtils.isBlank(strEparchyCode))
	        { 
				/**
				 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_3
				 * @author zhuoyingzhi
				 * @date 20170522
				 */
	        	//updTradeLTEBFn(input, "2007", "该号码不存在,请重新输入！","1");
	        	rspCode="2007";
	        	rspDesc = "该号码不存在,请重新输入！";
				/*************************end*************************/		        	
	            CSAppException.apperr(CrmCommException.CRM_COMM_1181, input.getString("MSISDN"));
	        }
			input.put("REMOVE_TAG", "0");
			String userId = InterforResalQry.queryColumnValue(input, "USER_ID");
			
			input.getString("TRADE_EPARCHY_CODE", strEparchyCode);
			input.put("CRM_USER_ID", userId);
//			dealList = parseParams(input);
			
			//获取最终要处理的业务，同时核对新办理的业务是否半停、全停
			String isStop = "0";	//0:不停机、1全停、2半停
			IDataset retnList = parseParams(input);
			if(retnList != null && retnList.size() > 0)
			{
				for(int i = 0, size = retnList.size(); i<size; i++)
				{
					if(retnList.getData(i).getString("INFO_CODE").equals("BAOCUSER") && retnList.getData(i).getString("MODIFY_TAG","").equals("0"))
					{
						isStop = "2";
						break;
					}
					else if(retnList.getData(i).getString("INFO_CODE").equals("STOPUSER") && retnList.getData(i).getString("MODIFY_TAG","").equals("0"))
					{
						isStop = "1";
						break;
					}
				}
				//将相关服务翻译成本地服务
				InterforResalQry.queryCommparaByBat(input,retnList);
				if(retnList != null && retnList.size() > 0)
				{
					dealList = retnList;
				}
			}
			
			if ("1001".equals(oprCode)) 
			{  //开户
				if (bIsDup && userId != null && !userId.equals("")) 
				{
				} 
				else
				{
					userId = SeqMgr.getUserId();
					input.put("CRM_USER_ID", userId);
					InterforResalQry.insertIntoLtebUser(input, dealList,userId);
				}
			}
			
			if (userId == null || userId.equals(""))
			{
				/**
				 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_4
				 * @author zhuoyingzhi
				 * @date 20170522
				 */
				// updTradeLTEBFn(input, "2007", "该用户不存在或未开户,不能进行此操作","1");
				 rspCode = "2007";
				 rspDesc = "该用户不存在或未开户,不能进行此操作";
				/*************************end*************************/
				 CSAppException.apperr(CrmCommException.CRM_COMM_1182);
			}
			
			if ("1008".equals(oprCode)) 
			{
				IData param = new DataMap();
				param.put("REMOVE_TAG", "2");
				param.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
				param.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
				param.put("LTEB_USER_ID", input.getString("USER_ID"));
				param.put("TNAME", "TF_F_LTEB_USER");
				param.put("SREF", "UPD_BY_LTEUSER_ID");
				InterforResalQry.updateInfo(param);
				
				param.put("TNAME", "TF_F_LTEB_USER_STATE");
				param.put("SREF", "DEL_ALL_USER_STATES");
				param.put("RSRV_STR1", input.getString("OPR_NUMB"));
				InterforResalQry.updateInfo(param);
			}
			genResInfo(input, userId);
			
			IData inParam = new DataMap();
			inParam.put("SERIAL_NUMBER", input.getString("MSISDN"));
			inParam.put("MSISDN", input.getString("MSISDN"));
			inParam.put("UPDATE_TIME", input.getString("UPDATE_TIME"));
			inParam.put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE"));
			inParam.put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
			inParam.put("TRADE_EPARCHY_CODE", strEparchyCode);
			inParam.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
			inParam.put("IMSI", input.getString("IMSI"));
			inParam.put("OPR_CODE",input.getString("OPR_CODE"));
			inParam.put("OPR_NUMB", input.getString("OPR_NUMB"));
			inParam.put("MSISDN", input.getString("MSISDN"));
			inParam.put("NEWIMSI", input.getString("NEWIMSI"));
			inParam.put("USER_ID", userId);
			inParam.put("LTE_USER_ID", input.getString("USER_ID"));
		    inParam.put("SVC_INFOS", dealList);
		    inParam.put("RES_DATA", input.getDataset("RES_DATA"));
		    inParam.put("IS_STOP", isStop);
		    
		    IDataset results = CSAppCall.call("SS.InterResaleRegSVC.tradeReg", inParam);
			if (IDataUtil.isNotEmpty(results)) 
			{
				strTradeId = results.first().getString("TRADE_ID", "0");
				strOrderId = results.first().getString("ORDER_ID", "0");
			}
			
		    /*try 
		    {*/
			/*} 
		    catch (Exception e) 
		    {
		    	if(logger.isInfoEnabled()) logger.info("SS.InterResaleRegSVC.tradeReg：" + e);
		    	IData inparams=new DataMap();
				inparams.put("TRADE_ID", "0");
				inparams.put("STATE", "1");
				inparams.put("RSPCODE", "9999");
				inparams.put("RSPDESC", "服务开通失败2");
				inparams.put("SERIAL_NUMBER", input.getString("MSISDN"));
				inparams.put("OPR_NUMB", input.getString("OPR_NUMB"));
				updTradeLTEB(inparams);
			}*/
			
			//if(logger.isInfoEnabled()) logger.info("SS.InterResaleRegSVC.tradeReg：TRADE_ID" + strTradeId + " ORDER_ID: " + strOrderId);
			
			/**
			 * REQ201607180025 关于非正常状态用户仍有话单产生的优化
			 * chenxy3 2016-09-29
			 * */
			if ("1004".equals(oprCode)) 
			{
				//if("1".equals(isStop) || "2".equals(isStop)){
				IData inparams=new DataMap();
				inparams.put("USER_ID", userId);
				inparams.put("SERVICE_ID", "0");
				Dao.executeUpdateByCodeCode("TF_B_TRADE_LTEB_USER_STATE", "UPD_TRADE_LTEB_USER_STATE", inparams, Route.getJourDb());
				inparams.put("TRADE_ID",strTradeId);
				String strCurMonth = SysDateMgr.getCurMonth();
				if(StringUtils.isNotBlank(strTradeId) && StringUtils.isNotBlank(strOrderId) && 
				  !"0".equals(strTradeId) && !"0".equals(strOrderId))
				{
					strCurMonth = strTradeId.substring(4, 6);
				}
				inparams.put("ACCEPT_MONTH", strCurMonth);
				inparams.put("MAIN_TAG","1");
				inparams.put("STATE_CODE",isStop);
				inparams.put("UPDATE_STAFF_ID ",input.getString("TRADE_STAFF_ID"));
				inparams.put("UPDATE_DEPART_ID",input.getString("TRADE_DEPART_ID"));
				inparams.put("REMARK","");
				inparams.put("RSRV_STR1",input.getString("MSISDN"));
				inparams.put("INST_ID",SeqMgr.getInstId());
				Dao.executeUpdateByCodeCode("TF_B_TRADE_LTEB_USER_STATE", "INS_TRADE_LTEB_USER_STATE", inparams, Route.getJourDb());
				//}
			} 
		} 
		catch (Exception e) 
		{
			if(logger.isInfoEnabled()) logger.info(e);
			//rspCode = input.getString("RSP_CODE",rspCode);
			//rspDesc = input.getString("RSP_DESC",rspDesc);
			
			if("0000".equals(rspCode))
			{
				//如果是没有在考虑的场景里面的错误
				String msg = e.getMessage();
				rspCode = "9999";
				//msg = (msg == null) ? "服务开通失败！":msg ;
				if(StringUtils.isBlank(msg))
				{
					msg = "服务开通失败1";
				}
				rspDesc = (msg.length() > 120) ? msg.substring(0,120):msg;
			}
			//SessionManager.getInstance().rollback(); 
            strTradeId = "0";
			/**
		     * REQ201605270005 转售业务保障方案需求
		     * @CREATED by chenxy3@2016-5-31
		     */
			IData inparams=new DataMap();
			inparams.put("TRADE_ID", strTradeId);
			inparams.put("STATE", "1");
			inparams.put("RSPCODE", rspCode);
			inparams.put("RSPDESC", rspDesc);
			inparams.put("SERIAL_NUMBER", input.getString("MSISDN"));
			inparams.put("OPR_NUMB", input.getString("OPR_NUMB"));
			updTradeLTEB(inparams);
		}
		
		IData inData = new DataMap();
		//更新下发表中执行的工单id
		inData.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		inData.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
		inData.put("TRADE_ID", strTradeId);
		inData.put("USER_ID", input.getString("USER_ID"));
		inData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		inData.put("OPR_CODE", oprCode);
		inData.put("TNAME", "TF_B_LTEB_ORDER_PF");
		inData.put("SREF", "UPD_PF_TRADE_ID");
		if(!"0".equals(strTradeId))
		{
			inData.put("REMARK", "成功！" + strOrderId);
			inData.put("STATUS", "1");
		}
		else
		{
			inData.put("REMARK", rspDesc + strTradeId + ":" + strOrderId);
			inData.put("STATUS", "4");
		}
		InterforResalQry.updateInfo(inData);
		
		returnData.put("OPR_NUMB", input.getString("OPR_NUMB"));
		
		/**
		 * QR-20170601-07_转售业务营业回单失败问题--影响考核
		 * @author zhuoyingzhi
		 * @date 20170602
		 */
		if("0000".equals(rspCode))
		{
			returnData.put("X_RSPTYPE", "0");
			returnData.put("X_RSPCODE", "0000");
			returnData.put("RSP_CODE", rspCode);
			returnData.put("RSP_DESC", rspDesc);
		}
		else
		{
			returnData.put("X_RSPTYPE", "2");
			returnData.put("X_RSPCODE", "2998");
			returnData.put("X_RESULTCODE", rspCode);
			returnData.put("X_RSPDESC", "失败");
			returnData.put("X_RESULTINFO", "失败");
			returnData.put("RSP_CODE", rspCode);
			returnData.put("RSP_DESC", rspDesc);
		}
		return returnData;
	}
	
	/**
	 * 比较变化前后的参数串，并把参数中的属性转换为crm中对应的服务并返回
	 * @param OldParams
	 * @param newParams
	 * @return 
	 * @throws Exception
	 */
	public IDataset parseParams(IData data) throws Exception{
		
		IDataset retnList = new DatasetList();
		IDataset oldParams = data.getDataset("OLD_PARAMS");
		IDataset newParams = data.getDataset("NEW_PARAMS");
		
		//针对销户操作不进行任务处理
		if ("1008".equals(data.getString("OPR_CODE")) || "1009".equals(data.getString("OPR_CODE"))) {
			retnList = new DatasetList();
		}else if (oldParams == null || newParams == null) {
			IDataset tempList = (oldParams == null) ? newParams:oldParams;
			retnList = (tempList == null)?new DatasetList():tempList;
			
		}else {
			IDataset newParseList = newParams;
			IDataset oldParseList = oldParams;
			
			for (int i = 0; i < newParseList.size(); i++) {
				boolean flag = true;
				String newCode = newParseList.getData(i).getString("INFO_CODE");
				String newNumName = data.getString("NEW_"+newCode+"NUM","");
				
				if ("NEWIMSI".equals(newCode) || (("STOPUSER".equals(newCode) 
						|| "BAOCUSER".equals(newCode)) &&"1003".equals(data.getString("OPR_CODE")))) {  //针对换卡把新imsi放到pd中
					continue;
				}else {
					Iterator iter = oldParseList.iterator();
					while (iter.hasNext()) {
						String oldCode = ((DataMap) iter.next()).getString("INFO_CODE");
						String OldNumName = data.getString("OLD_"+oldCode+"NUM","");
						
						if (newCode.equals(oldCode)) {
							flag = false;
							iter.remove();
							if (("CFU".equals(newCode) || "CFB".equals(newCode) 
									|| "CFNRY".equals(newCode) ||"CFNRC".equals(newCode))
									&& !newNumName.equals(OldNumName)) {
								flag = true;
							}
						}else if (("STOPUSER".equals(newCode) || "BAOCUSER".equals(newCode))
								&& ("STOPUSER".equals(oldCode) ||"BAOCUSER".equals(oldCode))) {
							iter.remove();
						}
					}
					
					if (flag){
						newParseList.getData(i).put("MODIFY_TAG", "0");
						retnList.add(newParseList.getData(i));
					}
				}
			}
			if(oldParseList != null && oldParseList.size() > 0){
				for (int i = 0; i < oldParseList.size(); i++) {
					IData oldParam = oldParseList.getData(i);
					oldParam.put("MODIFY_TAG", "1"); 
					retnList.add(oldParam);
				}
			}
		}
		
//		if (!retnList.isEmpty()) {
//			InterforResalQry.queryCommparaByBat(data,retnList);
//		}
		
		return retnList;
	}
	

	/**
	 * 组织资源数据
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset genResInfo(IData data,String userId) throws Exception {

		IDataset resList = new DatasetList();
		   String rspCode = "";
	        String rspDesc ="";
	        
        IData param1 = new DataMap();
        //OPE_TAG(为1的时候根据SERIAL_NUMBER查询，为2的时候根据IMSI查询) SERIAL_NUMBER、IMSI 
        param1.put("OPE_TAG", "1");
        param1.put("SERIAL_NUMBER", data.getString("MSISDN", ""));
        //查询手机号码是否在转售号码的号段内 
        IDataset resInfo =  ResCall.queryResellInfo(param1);
        if (IDataUtil.isEmpty(resInfo))
        {
            rspCode = "4005";
            rspDesc = "使用用户手机号码非法" + data.getString("MSISDN") + "不存在"; 
            data.put("RSP_CODE", rspCode);
            data.put("RSP_DESC", rspDesc);
          //  updTradeLTEBFn(data, "5999", "调资源接口获取号码段资源信息出错！","1");
            CSAppException.apperr(CrmCommException.CRM_COMM_1181, data.getString("MSISDN"));
        }

		IData resData = new DataMap();
		if ("1001".equals(data.getString("OPR_CODE"))) {
			// 号资源
			resData.put("RES_TYPE_CODE", "0");
			resData.put("RES_CODE", data.getString("MSISDN", ""));
			resData.put("IMSI", "0");
			resData.put("KI", "");
			resData.put("START_DATE", SysDateMgr.getSysTime());
			resData.put("END_DATE", SysDateMgr.getTheLastTime());
			resData.put("MODIFY_TAG", "0");
			resList.add(resData);
			
			// 卡资源 
			IData param = new DataMap();
			param.put("IMSI", data.getString("IMSI"));
			IDataset simInfo = ResCall.qryResaleSimByImsi(param);//CSAppCall.call("RM.SimCardInResaleSvc.qryResaleSimByImsi", param);
			
			if (IDataUtil.isEmpty(simInfo)) {
					/**
					 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_5
					 * @author zhuoyingzhi
					 * @date 20170522
					 */
				//	 updTradeLTEBFn(data, "5999", "调资源接口获取SIM资源信息出错！","1");
					/*************************end*************************/			
			    data.put("RSP_CODE", "4032");
			    data.put("RSP_DESC", "调资源接口获取SIM资源信息出错！");
				CSAppException.apperr(CrmCommException.CRM_COMM_1183, "调资源接口获取SIM资源信息出错！");
			}
			
            param.put("OPE_TAG", "3");
            param.put("SERIAL_NUMBER", data.getString("MSISDN", ""));
            //查询卡是否在转售号码的号段内
            IDataset simSegInfo =   ResCall.queryResellInfo(param);
            if (IDataUtil.isEmpty(simSegInfo))
            {
                rspCode = "4030";
                rspDesc = "IMSI不在特定号段内！"; 
                data.put("RSP_CODE", rspCode);
                data.put("RSP_DESC", rspDesc);
              //  updTradeLTEBFn(data, "5999", "调资源接口获取卡段与号码段资源信息不匹配！","1");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调资源接口获取SIM资源信息出错！");
            }
			
			IData imsiData = simInfo.getData(0);
			imsiData.put("RES_TYPE_CODE", "1");
			imsiData.put("RES_CODE", imsiData.getString("SIM_CARD_NO", ""));
			imsiData.put("START_DATE", SysDateMgr.getSysTime());
			imsiData.put("END_DATE", SysDateMgr.getTheLastTime());
			imsiData.put("MODIFY_TAG", "0");
			imsiData.put("RSRV_STR1", "0|1");
			imsiData.put("RSRV_STR5", imsiData.getString("OPC", ""));
			resList.add(imsiData);
			
			data.put("RES_DATA", resList);
		}
		
		if ("1002".equals(data.getString("OPR_CODE"))) {
			
			InterForResaleIntfBean	bean = new InterForResaleIntfBean();
			IData param = new DataMap();
			param.put("USER_ID", userId);
			param.put("IMSI", data.getString("IMSI"));
			param.put("RES_TYPE_CODE", "1");
			
			resData = InterforResalQry.queryUserIMEI(param);
	        if (IDataUtil.isEmpty(resData)) {
				/**
				 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_6
				 * @author zhuoyingzhi
				 * @date 20170523
				 */
				// updTradeLTEBFn(data, "2999", "该换卡用户没有SIM卡信息！","1");
				/*************************end*************************/	      
				   data.put("RSP_CODE", "4032");
	                data.put("RSP_DESC", "调资源接口获取SIM资源信息出错！");
	        	CSAppException.apperr(CrmCommException.CRM_COMM_1184, "该换卡用户没有SIM卡信息！");
			}
	        
			resData.put("END_DATE", SysDateMgr.getSysTime());
			resData.put("MODIFY_TAG", "1");
			resList.add(resData);

			param.clear();
			param.put("IMSI", data.getString("NEWIMSI"));
			IDataset simInfo = ResCall.qryResaleSimByImsi(param);//CSAppCall.call("RM.SimCardInResaleSvc.qryResaleSimByImsi", param);
			
			if (IDataUtil.isEmpty(simInfo)) {
				/**
				 * REQ201703210004_关于下发转售业务优化支撑改造方案的通知_7
				 * @author zhuoyingzhi
				 * @date 20170523
				 */
			//	 updTradeLTEBFn(data, "5999", "调资源接口获取SIM资源信息出错！","1");
				/*************************end*************************/				
				   data.put("RSP_CODE", "4032");
	                data.put("RSP_DESC", "调资源接口获取SIM资源信息出错！");
				CSAppException.apperr(CrmCommException.CRM_COMM_1185, "调资源接口获取SIM资源信息出错！");
			}
			
            param.put("OPE_TAG", "3");
            param.put("SERIAL_NUMBER", data.getString("MSISDN", ""));
            //查询卡是否在转售号码的号段内
            IDataset simSegInfo =   ResCall.queryResellInfo(param);
            if (IDataUtil.isEmpty(simSegInfo))
            {
                rspCode = "4030";
                rspDesc = "IMSI不在特定号段内！"; 
                data.put("RSP_CODE", rspCode);
                data.put("RSP_DESC", rspDesc);
              //  updTradeLTEBFn(data, "5999", "调资源接口获取卡段与号码段资源信息不匹配！","1");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调资源接口获取SIM资源信息出错！");
            }
			
			IData imsiData = simInfo.getData(0);
			imsiData.put("RES_TYPE_CODE", "1");
			imsiData.put("RES_CODE", imsiData.getString("SIM_CARD_NO", ""));
			imsiData.put("START_DATE", SysDateMgr.getSysTime());
			imsiData.put("END_DATE", SysDateMgr.getTheLastTime());
			imsiData.put("MODIFY_TAG", "0");
			imsiData.put("RSRV_STR1", "0|1");
			imsiData.put("RSRV_STR5", imsiData.getString("OPC", ""));
			
			resList.add(imsiData);
			data.put("RES_DATA", resList);
		}
		
//		if ("1004".equals(data.getString("OPR_CODE"))) {
//			
//			InterForResaleIntfBean	bean = new InterForResaleIntfBean();
//			IData param = new DataMap();
//			param.put("USER_ID", userId);
//			param.put("IMSI", data.getString("IMSI"));
//			param.put("RES_TYPE_CODE", "1");
//			
//			resData = InterforResalQry.queryUserIMEI(param);
//	        if (IDataUtil.isEmpty(resData)) {
//	        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该停机用户没有SIM信息！");
//			}
//	        
//			resData.put("END_DATE", SysDateMgr.getSysTime());
//			resData.put("MODIFY_TAG", "1");
//			resList.add(resData);
//
//			data.put("RES_DATA", resList);
//		} 
		
		return resList;
	}
	
	/**
     * REQ201605270005 转售业务保障方案需求
     * @CREATED by chenxy3@2016-5-31
     */
    public static void updTradeLTEB(IData inparams) throws Exception
    {   
    	String params = inparams.getString("PARAMS", "");
	    String stateCond = inparams.getString("STATE_COND", "");
	    String tradeId = inparams.getString("TRADE_ID", "");
	    String strState = inparams.getString("STATE", "");
	    
	    StringBuilder sql = new StringBuilder(1000);
	    /**
	     * REQ201703210004_关于下发转售业务优化支撑改造方案的通知
	     * @author zhuoyingzhi
	     * @date 
	     */
	    inparams.put("RSRV_STR2", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    
	    sql.append(" update TF_B_TRADE_LTEB ");
	    sql.append(" set RSPCODE=:RSPCODE, RSPDESC=:RSPDESC ,RSRV_STR2=:RSRV_STR2");
	    if(StringUtils.isNotBlank(strState))
	    {
	    	sql.append(" , STATE=:STATE");
	    }
	    if(!"".equals(tradeId))
	    {
	    	sql.append(" , RSRV_STR1=:TRADE_ID");
	    }
	    if(!"".equals(params))
	    {
	    	sql.append(" , PARAMS=:PARAMS ");
	    }
	    sql.append(" WHERE MSISDN=:SERIAL_NUMBER ");
	    sql.append(" AND OPR_NUMB=:OPR_NUMB "); 
	    if(!"".equals(stateCond))
	    {
	    	sql.append(" AND STATE=:STATE_COND "); 
	    }
	    
	    DBConnection conn = new DBConnection("cen1",true,false);
	    
	    try
        {
            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);

            dao.executeUpdate(conn, sql.toString(), inparams);
            conn.commit();

        }
        catch (Exception e)
        {
            conn.rollback();
            logger.error(e);
            throw e;
        }
        finally
        {
            conn.close();
        }
    }
    /**
     * REQ201703210004_关于下发转售业务优化支撑改造方案的通知
     *  修改记录信息
     *  <br/>
     *--处理标识：0 -- 待处理；1 -- CRM校验不通过（订单入表失败）；2 -- CRM登记台账成功；3 -- CRM订单完工；4 -- CRM应急完工  
     * @param data
     * @param RSPCODE
     * @param RSPDESC
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170522
     */
    public static void updTradeLTEBFn(IData data,String rspcode,String rspdesc,String state) throws Exception{
		IData inparams=new DataMap();
		inparams.put("STATE", state);
		inparams.put("RSPCODE", rspcode);
		inparams.put("RSPDESC", rspdesc);
		inparams.put("SERIAL_NUMBER", data.getString("MSISDN"));
		inparams.put("OPR_NUMB", data.getString("OPR_NUMB"));
		InterForResaleIntfBean.updTradeLTEB(inparams);
    }	
    
 
    public static IDataset queryOrderPF(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select TRADE_ID,STATUS,OPR_CODE,MSISDN,IMSI,USER_ID,OPR_NUMB,UPDATE_STAFF_ID TRADE_STAFF_ID,UPDATE_DEPART_ID TRADE_DEPART_ID,RSRV_STR1 TRADE_CITY_CODE,RSRV_STR2,UPDATE_TIME,REMARK  from TF_B_LTEB_ORDER_PF where 1=1 ");
        parser.addSQL(" and OPR_NUMB = :OPR_NUMB "); 
        parser.addSQL(" and MSISDN = :MSISDN ");
        parser.addSQL(" and IMSI = :IMSI ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN); 
        
    } 
    
	 
}