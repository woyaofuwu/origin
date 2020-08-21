package com.asiainfo.veris.crm.order.soa.person.busi.evaluecard;

import java.util.Date;

import org.apache.log4j.Logger;

import cn.com.tass.hnyd.hsm.TassHnydAPI;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.EValueCardInfoQry;

public class EValueCardOrderBean extends CSBizBean {
	Logger log = Logger.getLogger(EValueCardOrderBean.class);
	/** 
	 * 电子有价卡返销
	 * @param input
	 * @throws Exception
	 */
	public void cancelEValueCard(IData input) throws Exception {

		IData ibossData = new  DataMap();
		String transactionID = getTransactionID();
		ibossData.put("KIND_ID", "reSell_BOSS_0_0");
		ibossData.put("RESELLTYPE", input.getString("QRY_FLAG"));
		ibossData.put("ORI_TRANSACTIONID", input.getString("TRANSACTION_ID"));
		ibossData.put("CARD_NO", input.getString("CARD_NO"));
		ibossData.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		ibossData.put("SETTLE_DATE", SysDateMgr.getSysDate("yyyyMMdd"));
		ibossData.put("TRANSACTIONID", transactionID);
		IData retnInfo = new DataMap();
		try{
    		retnInfo = IBossCall.callHttpIBOSS4("IBOSS", ibossData).getData(0);
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
		if (!"0".equals(retnInfo.getString("X_RSPTYPE")) || !"0000".equals(retnInfo.getString("X_RSPCODE"))) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调有价卡返销接口失败，请重新办理!");
        } 
		
		input.put("CHANGE_TYPE", "1");
		input.put("OPER_TYPE", "1");
		input.put("CHANNEL_TYPE", "1");
		input.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		input.put("SETTLE_DATE", SysDateMgr.getSysDate("yyyyMMdd"));
		input.put("RESELL_TYPE", input.getString("QRY_FLAG"));
    	input.put("RESELL_TRANSACTIONID", input.getString("TRANSACTION_ID"));
    	input.put("RESELL_CARD_NO", input.getString("CARD_NO"));
    	input.put("CHANNEL_TYPE", "01");
    	input.put("TRANSACTIONID", transactionID);
    	input.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
    	
        EValueCardInfoQry.recordCardSaleInfo(input);
		
	}
	
	/**
	 * 营业厅电子有价卡销售
	 * @param input
	 * @throws Exception
	 */
public void sellStoreEValueCard(IData input) throws Exception{
		
    	IData ibossData = new  DataMap();
    	IData ibossResult  = new DataMap();
    	ibossData.put("KIND_ID", "sell_BOSS_0_0");
		ibossData.put("IDVALUE", input.getString("SERIAL_NUMBER"));
		ibossData.put("CHANNEL_TYPE", "01");
		ibossData.put("CARD_TYPE", input.getString("CARD_TYPE"));
		ibossData.put("CARD_MONEY", input.getString("CARD_MONEY"));
		ibossData.put("BUY_COUNT", input.getString("BUY_COUNT"));
		ibossData.put("HOME_PRO", "898");
		ibossData.put("EMAIL", input.getString("EMAIL"));
		ibossData.put("PAYMENT", "0");
		ibossData.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		ibossData.put("SETTLE_DATE", SysDateMgr.getSysDate("yyyyMMdd"));
		ibossData.put("TRANSACTIONID", getTransactionID());
		try {
    		ibossResult = IBossCall.callHttpIBOSS4("IBOSS", ibossData).getData(0); //调iboss进行有价卡购买
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
	        
        if ("0".equals(ibossResult.getString("X_RSPTYPE")) && "0000".equals(ibossResult.getString("X_RSPCODE"))) {
        	input.put("CHANGE_TYPE", "0");
        	IDataset cardInfos = ibossResult.getDataset("CARD_INFO"); //获取iboss接口返回的有价卡信息
        	if (cardInfos != null && !cardInfos.isEmpty()) {
        		if ( !TassHnydAPI.openHsm() ) {
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
        		}
				for (int i = 0; i < cardInfos.size(); i++) {
					IData cardInfo = cardInfos.getData(i);
					Date activeDate =  SysDateMgr.string2Date(cardInfo.getString("ACTIVE_DAYS"), SysDateMgr.PATTERN_TIME_YYYYMMDD);
					String activeStr = SysDateMgr.date2String(activeDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
					String initPasswd = TassHnydAPI.decryptByProvVK("01", cardInfo.getString("CARD_PASSWORD")); //将充值密码调用加密机解密   lihb3
					String password = new DecryptOrEncrypt().get3DESEncrypt(initPasswd,"1234").trim().intern();
					double cardMon = Double.valueOf(cardInfo.getString("CARD_MONEY","0"));
					StringBuilder sb = new StringBuilder("您购买的电子有价卡,卡号");
					sb.append((i+1)).append(":");
					sb.append(cardInfo.getString("CARD_NO")).append(",密码:").append("PWD")
					  .append(",面额:").append(String.format("%1$3.2f", cardMon / 100.0))
					  .append("元,有效期:").append(activeStr);
					cardInfo.put("TRANSACTIONID", ibossResult.getString("TRANSACTIONID"));
					cardInfo.put("SERIAL_NUMBER", ibossResult.getString("IDVALUE"));
					cardInfo.put("SETTLE_DATE", ibossResult.getString("SETTLE_DATE"));
					cardInfo.put("ACTION_TIME", ibossResult.getString("ACTION_TIME"));
					cardInfo.put("CHANNEL_TYPE", "01");
					cardInfo.put("CARD_TYPE", input.getString("CARD_TYPE"));
					cardInfo.put("CHANGE_TYPE", "0");
					cardInfo.put("OPER_TYPE","0");
					cardInfo.put("PAYMENT", "0");
					cardInfo.put("BUY_COUNT", input.getString("BUY_COUNT"));
					cardInfo.put("EMAIL", input.getString("EMAIL"));
					EValueCardInfoQry.recordCardSaleInfo(cardInfo);//插售卡记录表
					
					sb.append(",充值指令：CZ#充值卡密码#被充值号码.");
					//插短信表
					input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
					input.put("IN_MODE_CODE", getVisit().getInModeCode());
					
		        	EValueCardInfoQry.sms(input,sb.toString(),"0",password);
				}
				
				
			}
        	else
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取电子卡信息，请重新办理!");
        		
        	}
        }else {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取电子卡信息失败，请重新办理!");
		}
		
	}
	
	/**
	 * 营业厅电子有价卡批量销售
	 * @param input
	 * @throws Exception
	 */
	public IData batchSellStoreEValueCard(IData input) throws Exception{
	
		String strBatchSellReq= input.getString("X_BATSELLREQ_STR");
		String serialNumber = input.getString("SERIAL_NUMBER");
		String batchId = input.getString("TRANSACTION_ID");
		IDataset batchSellReqSet = new DatasetList(strBatchSellReq);
    	if (batchSellReqSet != null && !batchSellReqSet.isEmpty()) {
		    for (int i = 0; i < batchSellReqSet.size(); i++) {
		    	IData batchSellReq = batchSellReqSet.getData(i);
				IData record = new DataMap();
				record.put("TRANSACTION_ID", getTransID());
				record.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				record.put("SERIAL_NUMBER", serialNumber);
				record.put("CHANNEL_TYPE", batchSellReq.getString("CHANNEL_TYPE"));
				record.put("CARD_TYPE", batchSellReq.getString("CARD_TYPE"));
				record.put("CARD_BUSIPROP", batchSellReq.getString("CARD_BUSIPROP"));
				record.put("CARD_MONEY", Integer.parseInt(batchSellReq.getString("CARD_MONEY"))*100);
				record.put("BUY_COUNT", batchSellReq.getString("BUY_COUNT"));
				record.put("PAYMENT", "0");
				record.put("ACCEPT_DATE",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				record.put("SETTLE_DATE",SysDateMgr.getSysDate("yyyyMMdd"));
				record.put("PUBLIC_KEY_ID", batchSellReq.getString("PUBLIC_KEY_ID"));
				record.put("STATUS", "0");
				record.put("ORDER_NO", this.getOrderID(i+1));//3位，不足前面补0
				record.put("RSRV_STR2", batchId);//3位，不足前面补0      同一批次的流水号
				EValueCardInfoQry.recordBatchCardSaleInfo(record);//插售卡记录表				
		    }
    	}
    	return input;
	}
	/**
	 * 查询可以返销的电子有价卡销售数据
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset getCanCancelCardInfo(IData input) throws Exception{
		
		return  EValueCardInfoQry.queryCanCancelInfo(input);
	}
	

	/**
	 * 有价卡网厅补发
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData reSendCardInfo(IData input) throws Exception {

		IData ibossData = new  DataMap();
		IData ibossResult  = new DataMap();
		ibossData.put("KIND_ID", "reissue_BOSS_0_0");
		ibossData.put("REISSUE_TYPE", input.getString("REISSUE_TYPE"));
		ibossData.put("ORI_TRANSACTIONID", input.getString("ORI_TRANSACTIONID"));
		ibossData.put("CARD_NO", input.getString("CARD_NO"));
		ibossData.put("ACTION_TIME", input.getString("ACTION_TIME"));
		ibossData.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		ibossData.put("IDVALUE", input.getString("IDVALUE"));
		
		try {
    		ibossResult = IBossCall.callHttpIBOSS4("IBOSS", ibossData).getData(0); //调iboss进行有价卡购买
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
			
        if ("0".equals(ibossResult.getString("X_RSPTYPE")) && "0000".equals(ibossResult.getString("X_RSPCODE"))) {       	
			if ( !TassHnydAPI.openHsm() ) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
			}
			//获取返回参数中的卡信息
			IDataset cardInfos = ibossResult.getDataset("CARD_INFO");
        
        	if (cardInfos != null && !cardInfos.isEmpty()) {
				for (int i = 0; i < cardInfos.size(); i++) {
					IData cardInfo = cardInfos.getData(i);
					double cardMon = Double.valueOf(cardInfo.getString("CARD_MONEY","0"));
					
					Date activeDate =  SysDateMgr.string2Date(cardInfo.getString("ACTIVE_DAYS"), SysDateMgr.PATTERN_TIME_YYYYMMDD);
					String activeStr = SysDateMgr.date2String(activeDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
					//将充值密码调用加密机解密 01为省公司私钥索引  lihb3
					String initPasswd = TassHnydAPI.decryptByProvVK("01", cardInfo.getString("CARD_PASSWORD")); 
					String password = new DecryptOrEncrypt().get3DESEncrypt(initPasswd,"1234").trim().intern();
					StringBuilder sb = new StringBuilder("您购买的电子有价卡,卡号");
					sb.append((i+1)).append(":");
					sb.append(cardInfo.getString("CARD_NO")).append(",密码:").append("PWD")
					  .append(",面额:").append(String.format("%1$3.2f", cardMon / 100.0))
					  .append("元,有效期:").append(activeStr);	
					sb.append(",充值指令：CZ#充值卡密码#被充值号码.");
					//插短信表
		        	EValueCardInfoQry.sms(input,sb.toString(),"0",password);
				}
				
			}
        	else
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取电子卡信息，请重新办理!");	
		
        	}
        	
        } 
    	else
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "IBOSS获取电子卡信息，请重新办理!");	
	
    	}
		return ibossResult;
	}

	/**
	 * 外渠调用电子有价卡销售调用方法
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData sellEValueCardIntf(IData input) throws Exception {

		IData ibossData = new  DataMap();
		IData ibossResult  = new DataMap();
		ibossData.put("KIND_ID", "sell_BOSS_0_0");
		ibossData.put("IDVALUE", input.getString("IDVALUE"));
		ibossData.put("CHANNEL_TYPE", input.getString("CHANNEL_TYPE"));
		ibossData.put("CARD_TYPE", input.getString("CARD_TYPE"));
		ibossData.put("CARD_MONEY", input.getString("CARD_MONEY"));
		ibossData.put("BUY_COUNT", input.getString("BUY_COUNT"));
		ibossData.put("HOME_PRO", input.getString("HOME_PRO"));
		ibossData.put("EMAIL", input.getString("EMAIL"));
		ibossData.put("PAYMENT", input.getString("PAYMENT"));
		ibossData.put("ACTION_TIME", input.getString("ACTION_TIME"));
		ibossData.put("SETTLE_DATE", input.getString("SETTLE_DATE"));
		ibossData.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		ibossData.put("RESERVED", input.getString("RESERVED"));
		
		//针对电渠判断是不是重复提交，如果是重复提交直接返回成功
		if ("08".equals(input.getString("CHANNEL_TYPE"))) {
			IDataset restList = EValueCardInfoQry.checkOrderId(input.getString("ORDERID"));
			if (restList != null && restList.size() > 0) {
				
				IData retnMap = new DataMap();
				retnMap.put("TRANSACTIONID", restList.getData(0).getString("TRANSACTIONID"));
				retnMap.put("IDVALUE", restList.getData(0).getString("IDVALUE"));
				retnMap.put("ACTION_TIME", restList.getData(0).getString("ACTION_TIME"));
				retnMap.put("SETTLE_DATE", restList.getData(0).getString("SETTLE_DATE"));
				retnMap.put("X_RSPCODE", "0000");
				retnMap.put("X_RESULTCODE", "0");
				retnMap.put("X_RSPTYPE", "0");
				retnMap.put("CARD_INFO", restList);
				
				return retnMap;
			}
		}
		
		try {
    		ibossResult = IBossCall.callHttpIBOSS4("IBOSS", ibossData).getData(0); //调iboss进行有价卡购买
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
	        
        if ("0".equals(ibossResult.getString("X_RSPTYPE")) && "0000".equals(ibossResult.getString("X_RSPCODE"))) {
        	input.put("CHANGE_TYPE", "0");
        	IDataset cardInfos = ibossResult.getDataset("CARD_INFO"); //获取iboss接口返回的有价卡信息
        	if (cardInfos != null && !cardInfos.isEmpty()) {
        		if ( !TassHnydAPI.openHsm() ) {
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
        		}
				for (int i = 0; i < cardInfos.size(); i++) {
					IData cardInfo = cardInfos.getData(i);
					Date activeDate =  SysDateMgr.string2Date(cardInfo.getString("ACTIVE_DAYS"), SysDateMgr.PATTERN_TIME_YYYYMMDD);
					String activeStr = SysDateMgr.date2String(activeDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
					String initPasswd = TassHnydAPI.decryptByProvVK("01", cardInfo.getString("CARD_PASSWORD")); //将充值密码调用加密机解密   lihb3
					String password = new DecryptOrEncrypt().get3DESEncrypt(initPasswd,"1234").trim().intern();
					double cardMon = Double.valueOf(cardInfo.getString("CARD_MONEY","0"));
					StringBuilder sb = new StringBuilder("您购买的电子有价卡,卡号");
					sb.append((i+1)).append(":");
					sb.append(cardInfo.getString("CARD_NO")).append(",密码:").append("PWD")
					  .append(",面额:").append(String.format("%1$3.2f", cardMon / 100.0))
					  .append("元,有效期:").append(activeStr);
					cardInfo.put("TRANSACTIONID", ibossResult.getString("TRANSACTIONID"));
					cardInfo.put("SERIAL_NUMBER", ibossResult.getString("IDVALUE"));
					cardInfo.put("SETTLE_DATE", ibossResult.getString("SETTLE_DATE"));
					cardInfo.put("ACTION_TIME", ibossResult.getString("ACTION_TIME"));
					cardInfo.put("CHANNEL_TYPE", "01");
					cardInfo.put("CARD_TYPE", input.getString("CARD_TYPE"));
					cardInfo.put("CHANGE_TYPE", "0");
					cardInfo.put("OPER_TYPE","0");
					cardInfo.put("PAYMENT", "0");
					cardInfo.put("BUY_COUNT", input.getString("BUY_COUNT"));
					cardInfo.put("EMAIL", input.getString("EMAIL"));
					EValueCardInfoQry.recordCardSaleInfo(cardInfo);//插售卡记录表
					
					sb.append(",充值指令：CZ#充值卡密码#被充值号码.");
					//插短信表
					input.put("IDVALUE", input.getString("SERIAL_NUMBER"));
					input.put("IN_MODE_CODE", getVisit().getInModeCode());					
		        	EValueCardInfoQry.sms(input,sb.toString(),"0",password);
				}								
			}
        	else
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取电子卡信息，请重新办理!");
        		
        	}
        }else {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取电子卡信息失败，请重新办理!");
		}
        return ibossResult;
	}

	/**
	 * 外围接口查询有价卡信息
	 * @param input
	 * @throws Exception
	 */
	public IDataset queryValueCardInfo(IData input) throws Exception{
		
    	IData ibossData = new  DataMap();
    	ibossData.put("KIND_ID", "paymentQuery_BOSS_0_0");
		ibossData.put("CARD_NO", input.getString("CARD_NO"));
		ibossData.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		ibossData.put("IN_MODE_CODE", input.getString("IN_MODE_CODE"));
		ibossData.put("TRADE_EPARCHY_CODE", input.getString("TRADE_EPARCHY_CODE"));
		ibossData.put("TRADE_CITY_CODE", input.getString("TRADE_CITY_CODE"));
		ibossData.put("TRADE_DEPART_ID", input.getString("TRADE_DEPART_ID"));
		ibossData.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		
		IDataset retnData = new DatasetList();
    	try {
    		retnData = IBossCall.callHttpIBOSS4("IBOSS", ibossData); //调iboss
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
        return retnData;
	}
	
	/**
	 * 电子卡锁定、解锁、延期操作
	 */
	public IDataset lockOrUnlockEValueCard(IData input) throws Exception{
		IData data = new  DataMap();
		String opertype = input.getString("OPER_TYPE");
		String kindId = "";
		if("0".equals(opertype)){
			kindId = "cardLock_BOSS_0_0";  //锁定
		}else if("1".equals(opertype)){
			kindId = "cardUnLock_BOSS_0_0";//解锁
		}else if("2".endsWith(opertype)){
			kindId = "cardDefer_BOSS_0_0"; //延期
		}
		data.put("KIND_ID", kindId);
		data.put("BEGIN_CARDNO", input.getString("BEGIN_CARDNO"));
		data.put("END_CARDNO", input.getString("END_CARDNO"));
		data.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		data.put("ACTION_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		data.put("ACTION_ID", getVisit().getDepartId());    //营业厅编号
		data.put("ACTION_USERID", getVisit().getStaffId()); //操作员ID
		data.put("RESERVED", input.getString("RESERVED")); 
		IDataset resultList = new DatasetList();
    	try {
    		resultList = IBossCall.callHttpIBOSS4("IBOSS", data); //调iboss
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
        return resultList;
	}
	

	/**
	 * 安全密钥上行更新
	 */
	public IDataset upRsaPublicKey(IData input) throws Exception{
		
		String pwdProid = "";          //公钥ID
		String pwdProvalue = "";       //公钥信息
		String operflag = "";
		if("1".equals(input.getString("DONE_CODE"))){             //省公司
			String pwdid = EValueCardInfoQry.qryProPublicKeyID(); //判断省公钥是否已经存在
			if("".equals(pwdid)){
				pwdProid = SysDateMgr.getSysDateYYYYMMDD()+"898"+"0001";
				operflag = "1";  //新增
			}else{
				pwdProid = pwdid;
				operflag = "2";  //修改
			}
			if ( !TassHnydAPI.openHsm() ) {
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
			}
			pwdProvalue = TassHnydAPI.generateProvKeyPair("01");  //调加密机取公钥信息
			if ("".equals(pwdProvalue) || pwdProvalue == null) {   
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机导出公钥信息为空！");
			}         
			input.put("SECRPASSWD_ID", pwdProid);
			input.put("SECR_PASSWD",pwdProvalue);
			input.put("REMARK","省公司");
			input.put("PRORSA_FLAG","1");                         //省公钥标记
		}else if("".equals(input.getString("SECRPASSWD_ID"))){     
			pwdProid = SysDateMgr.getSysDateYYYYMMDD()+"898"+EValueCardInfoQry.getSecrPasswdId();
			input.put("SECRPASSWD_ID", pwdProid);
			operflag = "1";  //新增
		}else if("3".equals(input.getString("OPER_TYPE"))){
			operflag = "3";  //删除
		}else{
			operflag = "2";  //修改
		}
		
		IData data = new  DataMap();
		data.put("KIND_ID", "upRsaPublicKey_BOSS_0_0");
		data.put("TRANSACTIONID", input.getString("TRANSACTIONID"));
		data.put("SECRPASSWD_ID", input.getString("SECRPASSWD_ID"));
		data.put("PROVINCE_ID", "898");
		data.put("REQUEST_TYPE", operflag);
		data.put("SECR_PASSWD", input.getString("SECR_PASSWD"));
		data.put("RSRV_STR1",input.getString("REMARK"));
		
		IDataset resultList = new DatasetList();
    	try {
    		resultList = IBossCall.callHttpIBOSS4("IBOSS", data); //调iboss
        }catch(Exception ex){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS异常！");
    	}
    	if("00".equals(resultList.getData(0).getString("UPDATE_RESULT"))){
	    	input.put("HOME_PRO", "0");
			if("1".equals(operflag)){
				EValueCardInfoQry.recordRsaPublicKey(input);
			}else if("2".equals(operflag)){
				EValueCardInfoQry.updateRsaPublicKey(input);
			}else if("3".equals(operflag)){
				EValueCardInfoQry.deleteRsaPublicKey(input);
			}
    	}
        return resultList;
	}
	
	/**
	 * 安全密钥查询
	 */
	public IDataset qryRsaPublicKey(IData input, Pagination page) throws Exception{
		return EValueCardInfoQry.qryRsaPublicKey(input, page);
	}
	/**
	 * 安全密钥下行更新接口
	 */
	public IData downRsaPublicKey(IData input) throws Exception{
		String pwdid = IDataUtil.chkParam(input, "SECRPASSWD_ID");
		String passwd = IDataUtil.chkParam(input, "SECR_PASSWD");
	 	String requesttype = IDataUtil.chkParam(input, "REQUEST_TYPE");
	 	String transactionid = IDataUtil.chkParam(input, "TRANSACTIONID");
		
        //调加密机更新公钥信息
		if ( !TassHnydAPI.openHsm() ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "加密机连接失败！");
		}
		int ret = TassHnydAPI.updateCenterPK("02", passwd, null);    //成功返回0，其它失败
		if (ret != 0) {                                       
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "更新中心平台公钥保存至加密机失败！");
		}
		IData result = new DataMap();
    	result.put("TRANSACTIONID",transactionid);	
    	
		boolean flag = false;
		input.put("HOME_PRO", "1");
		if("1".equals(requesttype)){IDataset rstlist = EValueCardInfoQry.qryDownProPublicKeyID(pwdid);
			if(IDataUtil.isNotEmpty(rstlist)){
				log.debug("安全密钥下行更新接口:公钥ID："+pwdid+"已经存在，不允许新增");
				result.put("UPDATE_RESULT", "01");
				return result;
			}
			flag = EValueCardInfoQry.recordRsaPublicKey(input);
		}else if("2".equals(requesttype)){
			flag = EValueCardInfoQry.updateRsaPublicKey(input);
		}
    	String updateresult =(flag == true ? "00" :"01");
		result.put("UPDATE_RESULT", updateresult);
        return result;
	}
	
	/**
	 * 生成流水
	 * @return
	 * @throws Exception
	 */
	public String getTransactionID() throws Exception{
		return "898"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+"000001"; 
	}
	
	public String getTransID() throws Exception{
		String eparchcode=getVisit().getLoginEparchyCode();
		String id = SeqMgr.getInstId(eparchcode);
		return "898"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+id.substring(id.length()-6);
	}
	/**
	 * 生成序列号
	 * @return
	 * @throws Exception
	 */
	public String getOrderID(int index) throws Exception{
		String orderId = "001";
		if (index>0 && index<10) {
			orderId = "00" + index;
		}
		
		if (index>99 && index<100) {
			orderId = "0" + index;
		}
		
		if (index>999 && index<1000) {
			orderId = "" + index;
		}
		return orderId;
	}
}
