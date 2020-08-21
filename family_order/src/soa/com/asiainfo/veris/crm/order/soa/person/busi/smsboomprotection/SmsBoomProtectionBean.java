package com.asiainfo.veris.crm.order.soa.person.busi.smsboomprotection;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class SmsBoomProtectionBean  extends CSBizBean{
	private static transient Logger logger = Logger.getLogger(SmsBoomProtectionBean.class);
	
	
	public IDataset qryProtectinfoBySerialNum(IData param,Pagination page) throws Exception{
	    IDataset dataset =  Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_SERIAL_NO", param,page,Route.CONN_CRM_CEN);
	    return dataset;
	}
	
	public IDataset qryProtectinfoByAccessNum(IData param,Pagination page) throws Exception{
	    IDataset dataset =  Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_ACCESS_NO", param,page,Route.CONN_CRM_CEN);
	    return dataset;
	}
	
	public IDataset qryProtectinfo(IData param,Pagination page) throws Exception{
	    IDataset dataset =  Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_ALL", param,page,Route.CONN_CRM_CEN);
	    return dataset;
	}
	//REQ201904080006关于开发短信炸弹及互联网中心密码重置接口的需求 wuhao5
	public IData qrySMSBombStatRecInf(IData input) throws Exception {
    	if("".equals(input.getData("params").getString("userMobile","")))
    	{
    		IData result = new DataMap();
            
            IData object = new DataMap();
            object.put("result", result);
            object.put("respCode", "1");
            object.put("respDesc", "noSN");
            
            IData returnData = new DataMap();
            returnData.put("rtnCode", "1");
            returnData.put("rtnMsg", "未输入查询号码!");
            returnData.put("object", object);
            
            return returnData;   
    	}
		input.put("ACCESS_NO", input.getData("params").getString("userMobile"));
		IDataset dataset =  Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_EFF_BY_SN", input,Route.CONN_CRM_CEN);
		IData result = new DataMap();
		if(dataset.size() > 0 && IDataUtil.isNotEmpty(dataset))
		{
			IData data = dataset.getData(0);
			String curProtectStat = data.getString("STATUS");
			//判断生效截止时间,若大于当前时间,且防护状态为开通,才确定状态为开通
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmss");	
			Date sysDate = new Date();
			Date effDate = df.parse(data.getString("EXPIRE_DATE"));
			
			if("0".equals(curProtectStat) && effDate.after(sysDate)){
				curProtectStat = "1";
			}
			else{
				curProtectStat = "2";
			}
			String opChannelName = StaticUtil.getStaticValue("SMSPROT_CHANNEL", data.getString("CHANNEL_ID"));
			//状态 1:开 2:关
			result.put("curProtectStat", curProtectStat);
			//流水
			result.put("tradeId", data.get("RECV_ID"));
			//生效截止时间 格式YYYYMMDDHH24MISS
			result.put("effectiveTime", data.get("EXPIRE_DATE"));
			//受理时间 格式YYYYMMDDHH24MISS
			result.put("opTime", data.get("ACCEPT_TIME"));
			//受理渠道 格式 ----渠道编码：渠道名称
			result.put("opChannelName", opChannelName);
			//受理工号
			result.put("operId", data.get("CREATE_STAFF_ID"));
			//受理号码	
			result.put("userMobile", data.get("SERIAL_NO"));	
			//备注---由5个预留字段拼接,以"|"符号分割
			result.put("remark", "");
            
            IData object = new DataMap();
            object.put("result", result);
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            IData returnData = new DataMap();
            returnData.put("rtnCode", "0");
            returnData.put("rtnMsg", "成功!");
            returnData.put("object", object);
            
            return returnData;   
		}        
        IData object = new DataMap();
        object.put("result", result);
        object.put("respCode", "9");
        object.put("respDesc", "noData");
        
        IData returnData = new DataMap();
        returnData.put("rtnCode", "9");
        returnData.put("rtnMsg", "没有符合条件的记录!");
        returnData.put("object", object);
	    return returnData;
	}
	
	
	public void updateProtectInfo(IData param) throws Exception{
		 //获得唯一编码
		String  indictSeq =getIndictSequence(getProvinceID());
	    param.put("RECV_ID",indictSeq);
		syncSmsProtectedList(param);
		IData data = new DataMap();
		data.put("EXPIRE_DATE", param.getString("EXPIRE_DATE"));
		data.put("UPDATE_TIME", param.getString("UPDATE_TIME"));
		data.put("ACCESS_NO", param.getString("ACCESS_NO"));
		data.put("SERIAL_NO", param.getString("SERIAL_NO"));
		data.put("RECV_ID", param.getString("RECV_ID"));
		Dao.executeUpdateByCodeCode("TI_O_SMS_PROTEGE", "UPDATE_PROTECTLIST", data,Route.CONN_CRM_CEN);
		IData tailtrade = inserthistrade(param.getString("ACCESS_NO"));
		inserttradesmsprotege(param,tailtrade,"3");
		sendsms(param,tailtrade,"3");
	}
	
	
	public void deleteProtectInfo(IData param) throws Exception{
		 //获得唯一编码
		String  indictSeq =getIndictSequence(getProvinceID());
	    param.put("RECV_ID",indictSeq);
		syncSmsProtectedList(param);
		Dao.executeUpdateByCodeCode("TI_O_SMS_PROTEGE", "DEL_PROTECTLIST", param,Route.CONN_CRM_CEN);
		IData tailtrade = inserthistrade(param.getString("ACCESS_NO"));
		inserttradesmsprotege(param,tailtrade,"2");
		sendsms(param,tailtrade,"2");
	}
	
	
	public void insertProtectInfo(IData param) throws Exception{
		
		String serialNum=param.getString("SERIAL_NO","");
		String accessNum=param.getString("ACCESS_NO","");//被保护号码
		
		//归属省ID
		IDataset results = qryEpareycodeout(accessNum);
		if(IDataUtil.isEmpty(results)){
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "受保护号码不是移动号码");
		}
		if(IDataUtil.isEmpty(qryEpareycodeout(serialNum))){
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "受理号码不是移动号码");
		}
		param.put("PROV_ID",results.getData(0).getString("PROV_CODE"));
	     //获得唯一编码
	    String   provinceID = getProvinceID();
	    String  indictSeq =getIndictSequence(provinceID);
	    param.put("RECV_ID",indictSeq);
	     
	    syncSmsProtectedList(param);
	    Dao.insert("TI_O_SMS_PROTEGE", param,Route.CONN_CRM_CEN);
		IData tailtrade = inserthistrade(param.getString("ACCESS_NO"));
		inserttradesmsprotege(param,tailtrade,"1");
		sendsms(param,tailtrade,"1");
		}
	
	//同步IBOSS
	public void syncSmsProtectedList(IData param) throws Exception{
		
	    logger.debug("-------------------同步IBOSS入参为："+param.toString());
	     String provinceCode = param.getString("PROV_ID");
		 String channelCode=param.getString("CHANNEL_ID", "");
		 String serialNumber=param.getString("SERIAL_NO", "");
		 String accessNumber=param.getString("ACCESS_NO");
		 String staff=getVisit().getStaffId() + "-" + getVisit().getStaffName();
	 	 String acceptTime=param.getString("ACCEPT_TIME");
		 String endTime=param.getString("EXPIRE_DATE");
	 	 String updateType=param.getString("SMSBOMB_BUSINESS_TYPE");
	 	 String indictSeq=param.getString("RECV_ID");
	
	
		 IData result = IBossCall.syncProtectedSMSBombList(indictSeq, accessNumber,serialNumber,
	          staff, channelCode, provinceCode, acceptTime, endTime, updateType);
		 logger.debug("IBOSS响应参数:" + result.toString());
		 if (!"0000".equals(result.getString("X_RSPCODE", ""))){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,
					"落地方处理失败:" + result.getString("X_RESULTINFO"));
		}
	}
	/**
	 * 短信炸弹保护名单同步
	 * @param param
	 * @throws Exception
	 */
	public IData syncProtectedSMSBombList(IData params) throws Exception{
		
		IData result = syncParmCheck(params);//入参校验
		if(IDataUtil.isNotEmpty(result)){
			return result;
		}
	    IDataset infos = null;
	    IData param = new DataMap();
	
		String channelCode = params.getString("CONTACT_CHANNEL");//接触渠道
		String accessNumber = params.getString("ACCEPT_NUMBER");//受理号码
		String acceptTime = params.getString("ACCEPT_TIME");//受理时间
		String provinceCode = params.getString("PROVINCE");//被保护号码归属省ID
		String serialNumber = params.getString("SUB_NUMBER");//被保护号码
		String endTime = params.getString("EFFECTIVE_TIME");//生效截止时间,格式为：YYYYMMDDHH（24）MMSS
		String updateType = params.getString("UPDATE_TYPE");//更新方式1：增加2：删除3：更新
		String remark = params.getString("REMARK","");//备注
		IData staffData= params.getData("CRMPF_PUB_INFO");
		
		if(IDataUtil.isEmpty(qryEpareycodeout(serialNumber))){
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "受保护号码不是移动号码");
		}
		if(IDataUtil.isEmpty(qryEpareycodeout(accessNumber))){
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "受理号码不是移动号码");
		}
		if(IDataUtil.isEmpty(staffData)){
			return syncParmCheckResult("CRMPF_PUB_INFO");
		}
		//员工信息
		String staffID=staffData.getString("STAFF_ID", "");
		IData staffInfo= UStaffInfoQry.qryStaffAreaInfoByStaffId(staffID);
		if(IDataUtil.isEmpty(staffInfo)){
			return failResult("员工信息不存在[STAFF_ID="+staffID+"]");
		}
		String staff = staffID+"-"+staffInfo.getString("STAFF_NAME","");
		
		
		String nowdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		int tag = endTime.compareTo(nowdate);
		String oneyearlatedate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		String oneyearlatetime = DateFormatUtils.format(SysDateMgr.string2Date(oneyearlatedate, "yyyy-MM-dd"), "yyyyMMddHHmmss");
		int oneyearlatetag = endTime.compareTo(oneyearlatetime);
		//流水号
		String   provinceID = this.getProvinceID();
		String  indictSeq = getIndictSequence(provinceID);
		
		param.put("CHANNEL_ID", channelCode);
		param.put("SERIAL_NO", accessNumber);
		param.put("ACCEPT_TIME", acceptTime);
		param.put("PROV_ID", provinceCode);
		param.put("ACCESS_NO",serialNumber);
		param.put("EXPIRE_DATE", endTime);
		param.put("RECV_ID", indictSeq);
		param.put("CREATE_STAFF_ID", staffID);
	
		if("1".equals(updateType)){//增加
				if(tag<0){//判断时间大于今天
					return failResult("生效截止时间不能小于当前时间！");
				}
				
				if(oneyearlatetag>0){//时间小于一年
					return failResult("生效截止时间不能超过当前时间一年！");
				}
				
				IData condition = new DataMap();
				condition.put("ACCESS_NO", serialNumber);//被保护号码
				infos =Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_ACCESS_NO", condition,Route.CONN_CRM_CEN);
		       	 if(IDataUtil.isNotEmpty(infos)){
		       		 return failResult("开通失败，该用户已存在！");
		       	 }
		       	 
	   		IData ibossResult = IBossCall.syncProtectedSMSBombList(indictSeq, serialNumber, accessNumber,
	   				staff, channelCode, provinceCode, acceptTime, endTime, updateType);
		       	if (ibossResult == null || ibossResult.size() == 0) { // 未找到记录
		       		return failResult("调用一级boss平台接口无反馈!");
	        }
		       	if (!"0".equals(ibossResult.get("X_RSPCODE")) && !"00".equals(ibossResult.get("X_RSPCODE")) && !"000".equals(ibossResult.get("X_RSPCODE")) &&
	                !"0000".equals(ibossResult.get("X_RSPCODE"))) {
	        	return failResult("调用一级boss平台接口失败!");
	        } 
		     
				param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				param.put("STATUS","0");//名单状态

				Dao.insert("TI_O_SMS_PROTEGE", param,Route.CONN_CRM_CEN);
				IData tailtrade = inserthistrade(accessNumber);
				inserttradesmsprotege(param,tailtrade,"1");
				sendsms(param,tailtrade,"1");
				
		     }
		else if("2".equals(updateType)){//2：删除
			IData condition = new DataMap();
			condition.put("ACCESS_NO", serialNumber);
			condition.put("SERIAL_NO", accessNumber);
			infos =Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_ALL", condition,Route.CONN_CRM_CEN);
	       	if(IDataUtil.isEmpty(infos)){
	       		return failResult("该用户不存在！");
	       	}
	
	
	       	IData ibossResult = IBossCall.syncProtectedSMSBombList(indictSeq, serialNumber, accessNumber,
	   				staff, channelCode, provinceCode, acceptTime, endTime, updateType);
	       	if (ibossResult == null || ibossResult.size() == 0) { // 未找到记录
	       		return failResult("调用一级boss平台接口无反馈!");
	        } 
	       	if (!"0".equals(ibossResult.get("X_RSPCODE")) && !"00".equals(ibossResult.get("X_RSPCODE")) && !"000".equals(ibossResult.get("X_RSPCODE")) &&
	                !"0000".equals(ibossResult.get("X_RSPCODE"))) {
	        	return failResult("调用一级boss平台接口失败!");
	        } 
	
			Dao.executeUpdateByCodeCode("TI_O_SMS_PROTEGE", "DEL_PROTECTLIST", param,Route.CONN_CRM_CEN);
			IData tailtrade = inserthistrade(accessNumber);
			inserttradesmsprotege(param,tailtrade,"2");
			sendsms(param,tailtrade,"2");
		}
		else if("3".equals(updateType)){//3：更新
			//判断时间大于今天
				 if(tag<0){
					return failResult("生效截止时间不能小于当前时间！");
				 }
				 //时间小于一年
				 if(oneyearlatetag>0){
					return failResult("生效截止时间不能超过当前时间一年！");
				 }
			IData condition = new DataMap();
			condition.put("ACCESS_NO", serialNumber);
			condition.put("SERIAL_NO", accessNumber);
				infos =Dao.qryByCode("TI_O_SMS_PROTEGE", "SEL_BY_ALL", condition,Route.CONN_CRM_CEN);
			if(IDataUtil.isEmpty(infos)){
				return failResult("该用户不存在！");
	       	 }
	
			IData ibossResult = IBossCall.syncProtectedSMSBombList(indictSeq, serialNumber, accessNumber,
	   				staff, channelCode, provinceCode, acceptTime, endTime, updateType);
			if (ibossResult == null || ibossResult.size() == 0) { // 未找到记录
	       		return failResult("调用一级boss平台接口无反馈!");
	        } 
	       	if (!"0".equals(ibossResult.get("X_RSPCODE")) && !"00".equals(ibossResult.get("X_RSPCODE")) && !"000".equals(ibossResult.get("X_RSPCODE")) &&
	                !"0000".equals(ibossResult.get("X_RSPCODE"))) {
	       		return failResult("调用一级boss平台接口失败!");
	        } 
	     
				param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				Dao.executeUpdateByCodeCode("TI_O_SMS_PROTEGE", "UPDATE_PROTECTLIST", param,Route.CONN_CRM_CEN);
				IData tailtrade = inserthistrade(accessNumber);
				inserttradesmsprotege(param,tailtrade,"3");
				sendsms(param,tailtrade,"3");
		}
		IData result2 = new DataMap();
		result.put("RTN_CODE", "0");
		result.put("RTN_MSG", "成功!");
		result2.put("RESP_CODE", "0");
		result2.put("RESP_DESC", "success");
		result.put("OBJECT", result2);
		
		return result;
		
	}
	
	/**
	 * 入参校验
	 * @param data
	 */
	private IData syncParmCheck(IData data){
		//接触渠道
		if(StringUtils.isEmpty(data.getString("CONTACT_CHANNEL",""))) {
			return syncParmCheckResult("CONTACT_CHANNEL");
		}
		//受理号码
		if(StringUtils.isEmpty(data.getString("ACCEPT_NUMBER",""))) {
			return syncParmCheckResult("ACCEPT_NUMBER");
		}
		//受理时间
		if(StringUtils.isEmpty(data.getString("ACCEPT_TIME",""))) {
			return syncParmCheckResult("ACCEPT_TIME");
		}
		//被保护号码归属省ID
		if(StringUtils.isEmpty(data.getString("PROVINCE",""))) {
			return syncParmCheckResult("PROVINCE");
		}
		//被保护号码
		if(StringUtils.isEmpty(data.getString("SUB_NUMBER",""))) {
			return syncParmCheckResult("SUB_NUMBER");
		}
		//生效截止时间
		if(StringUtils.isEmpty(data.getString("EFFECTIVE_TIME",""))) {
			return syncParmCheckResult("EFFECTIVE_TIME");
		}
		//更新方式
		if(StringUtils.isEmpty(data.getString("UPDATE_TYPE",""))) {
			return syncParmCheckResult("UPDATE_TYPE");
		}
		return new DataMap();
	}
	
	/**
	 * 接口必传入参为空结果返回
	 * @param str
	 * @return
	 */
	private IData syncParmCheckResult(String str){
		return failResult("接口入参["+str+"]参数不能为空！");
	}
	
	
	/**
	 * 失败结果返回
	 * @param str
	 * @return
	 */
	private IData failResult(String str){
		IData result = new DataMap();
		IData data = new DataMap();
		result.put("RTN_CODE", "-9999");
		result.put("RTN_MSG", "失败,"+str);
		
		data.put("RESP_CODE", "-9999");
		data.put("RESP_DESC", "失败,"+str);
		
		result.put("OBJECT", data);
		
		return result;
	}
	/**
	 * //服务请求标识 数据构造： YYYYMMDD＋CSVC＋3位省代码＋7位流水号
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getIndictSequence(String provinceID) throws Exception
	{	
	    String tempSeq = SeqMgr.getCustContact();
	    String finalSeq = "";
	    String indictSeq = "";
	
	    if (tempSeq.length() == 7)
	    {
	        finalSeq = tempSeq;
	    }
	    else
	    {
	        finalSeq = tempSeq.substring(tempSeq.length() - 7, tempSeq.length());
	    }
	
	    indictSeq = SysDateMgr.getNowCycle() + "CSVC" + provinceID + finalSeq;
	    return indictSeq;
	}
	
	/**
	 * 插入台账历史表
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 * add by  huangzl3
	 */
	public IData inserthistrade(String SERIAL_NUMBER) throws Exception
    {
        UcaData ucadata = UcaDataFactory.getNormalUca(SERIAL_NUMBER);

        String eparchyCode = CSBizBean.getTradeEparchyCode();
        String systime = SysDateMgr.getSysTime();
        String tradeId = SeqMgr.getTradeId();
        IData inparam = new DataMap();
        String netTypeCode = ucadata.getUser().getNetTypeCode();
        if (StringUtils.isBlank(netTypeCode))
            netTypeCode = "00";

        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", SeqMgr.getOrderId());
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", "2482");// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "9");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inparam.put("CUST_ID", ucadata.getCustId());
        inparam.put("CUST_NAME", ucadata.getCustomer().getCustName());
        inparam.put("USER_ID", ucadata.getUser().getUserId());
        inparam.put("ACCT_ID", ucadata.getAcctId());
        inparam.put("SERIAL_NUMBER", SERIAL_NUMBER);
        inparam.put("NET_TYPE_CODE", netTypeCode);
        inparam.put("EPARCHY_CODE", eparchyCode);
        inparam.put("CITY_CODE", ucadata.getUser().getCityCode());
        inparam.put("PRODUCT_ID", ucadata.getProductId());
        inparam.put("BRAND_CODE", ucadata.getBrandCode());
        inparam.put("ACCEPT_DATE", systime);
        inparam.put("UPDATE_TIME", systime);
        inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", eparchyCode);
        inparam.put("OPER_FEE", "0");
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "0");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("FEE_STATE", "0");
        inparam.put("FINISH_DATE", systime);
        inparam.put("EXEC_TIME", systime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("PF_WAIT", "0");// 是否发开通
        inparam.put("REMARK", "短信炸弹受理入台帐历史表");
        Dao.insert("TF_BH_TRADE", inparam,Route.getJourDb(eparchyCode));
        return inparam;
    }
	
	
	/**
	 * 插入TF_B_TRADE_SMS_PROTEGE表
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 * add by  huangzl3
	 */
	public void inserttradesmsprotege(IData params1,IData params2,String type) throws Exception
    {	
			String nowdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String eparchyCode = CSBizBean.getTradeEparchyCode();
			IData inparam = new DataMap();
	        inparam.put("TRADE_ID", params2.getString("TRADE_ID"));
	        inparam.put("ACCEPT_MONTH", params2.getString("ACCEPT_MONTH"));
	        inparam.put("USER_ID", params2.getString("USER_ID"));
	        inparam.put("RECV_ID", params1.getString("RECV_ID"));
	        inparam.put("CHANNEL_ID", params1.getString("CHANNEL_ID"));
	        inparam.put("SERIAL_NO", params1.getString("SERIAL_NO"));
	        inparam.put("PROV_ID", getProvinceID());
	        inparam.put("ACCESS_NO", params1.getString("ACCESS_NO"));
	        if(type == "2"){	   
	        	inparam.put("EXPIRE_DATE", nowdate);
	        }else{
	        	inparam.put("EXPIRE_DATE", params1.getString("EXPIRE_DATE"));
	        }
	        inparam.put("ACCEPT_TIME", nowdate);
	        inparam.put("MODIFY_TAG", type);
	        inparam.put("CREATE_STAFF_ID", params2.getString("UPDATE_STAFF_ID"));
	        inparam.put("UPDATE_TIME", nowdate);
	        if(type == "2"){	        	
	        	inparam.put("STATUS", "1");
	        }else{
	        	inparam.put("STATUS", "0");
	        }
	        Dao.insert("TF_B_TRADE_SMS_PROTEGE", inparam,Route.getJourDb(eparchyCode));
    }
	/**
	 * 下发短信
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 * add by  huangzl3
	 */
	public void sendsms(IData params1,IData params2,String type) throws Exception{
		IData SMSinfo = new DataMap();
		String sysDate = SysDateMgr.getSysTime();
		String sms_notice_id=SeqMgr.getSmsSendId();
		SMSinfo.put("SMS_NOTICE_ID",sms_notice_id);
		SMSinfo.put("PARTITION_ID", sms_notice_id.substring(sms_notice_id.length() - 4));
		SMSinfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		SMSinfo.put("BRAND_CODE", "");
		SMSinfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		SMSinfo.put("CHAN_ID","2482");//短信渠道编码:平台编码
		SMSinfo.put("SMS_NET_TAG","0");
		SMSinfo.put("SEND_OBJECT_CODE","6");	
		SMSinfo.put("SEND_COUNT_CODE","1");
		SMSinfo.put("SEND_TIME_CODE","1");
		SMSinfo.put("RECV_OBJECT_TYPE","00");//被叫对象类型:00－手机号码
		SMSinfo.put("RECV_OBJECT",params1.getString("ACCESS_NO"));//被叫对象:传手机号码
		SMSinfo.put("RECV_ID",params2.getString("USER_ID"));//被叫对象标识:传用户标识
		SMSinfo.put("SMS_TYPE_CODE","20");//短信类型:20-业务通知
		SMSinfo.put("SMS_KIND_CODE","02");//短信种类:02－短信通知
		SMSinfo.put("NOTICE_CONTENT_TYPE","0");//短信内容类型:0－指定内容发送
		SMSinfo.put("REFERED_COUNT","0");
		SMSinfo.put("FORCE_REFER_COUNT","1");//指定发送次数
		SMSinfo.put("FORCE_OBJECT", "10086");
		SMSinfo.put("FORCE_START_TIME",sysDate);//指定发送次数
		SMSinfo.put("FORCE_END_TIME", "");
		SMSinfo.put("SMS_PRIORITY",1000);//短信优先级
		SMSinfo.put("REFER_TIME",sysDate);//提交时间
		SMSinfo.put("REFER_STAFF_ID",CSBizBean.getVisit().getStaffId());//提交员工
		SMSinfo.put("REFER_DEPART_ID",CSBizBean.getVisit().getDepartId());//提交部门
		SMSinfo.put("DEAL_TIME",sysDate);//处理时间
		SMSinfo.put("DEAL_STAFFID", CSBizBean.getVisit().getStaffId());
		SMSinfo.put("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId());
		SMSinfo.put("DEAL_STATE","15");//处理状态:0－未处理
		SMSinfo.put("REMARK", "短信炸弹短信下发");
		SMSinfo.put("MONTH",Integer.parseInt(sysDate.substring(5, 7)));
		SMSinfo.put("DAY",Integer.parseInt(sysDate.substring(8, 10)));
		String SMSCONTENT = "";
		String EXPIRE_DATE = params1.getString("EXPIRE_DATE","");
		if(type == "1"){
			SMSCONTENT = "您好！您已成功开通“短信炸弹”应急防护服务，该服务将于"+EXPIRE_DATE.substring(0, 4)+
					"年"+EXPIRE_DATE.substring(4, 6)+
					"月"+EXPIRE_DATE.substring(6, 8)+
					"日"+EXPIRE_DATE.substring(8, 10)+
					"时"+EXPIRE_DATE.substring(10, 12)+
					"分自动失效，您可通过拨打10086或到当地移动营业厅提前取消防护服务，感谢您的理解与支持！【中国移动】";
		}else if(type == "2"){
			SMSCONTENT = "您好！您已成功取消“短信炸弹”应急防护服务，感谢您的理解与支持！【中国移动】";
		}else{
			SMSCONTENT = "您好！您已成功变更“短信炸弹”应急防护有效期，该服务将于"+EXPIRE_DATE.substring(0, 4)+
					"年"+EXPIRE_DATE.substring(4, 6)+
					"月"+EXPIRE_DATE.substring(6, 8)+
					"日"+EXPIRE_DATE.substring(8, 10)+
					"时"+EXPIRE_DATE.substring(10, 12)+
					"分自动失效，感谢您的理解与支持！【中国移动】";
		}
		SMSinfo.put("NOTICE_CONTENT", SMSCONTENT);// 短信内容类型:0－指定内容发送
		
		SmsSend.insSms(SMSinfo);
	}	
	/**
	 * 获取省代码
	 * 
	 * @param cycle
	 * @return
	 * @throws Exception
	 */
	public String getProvinceID() throws Exception
	{
	    String provinceID = "";
	
	    if ("XINJ".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "991";
	    }
	    if ("HAIN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "898";
	    }
	    if ("HNAN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "731";
	    }
	    if ("QHAI".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "971";
	    }
	    if ("SHXI".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "290";
	    }
	    if ("TJIN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "220";
	    }
	    if ("YUNN".equals(getVisit().getProvinceCode()))
	    {
	        provinceID = "871";
	    }
	    return provinceID;
	}
	
	public static IDataset qryEpareycodeout(String serialNumber) throws Exception
	{
	    IData param = new DataMap();
	    param.put("SERIAL_NUMBER", serialNumber);
	
	    SQLParser parser = new SQLParser(param);
	    parser.addSQL(" select b.*,a.asp from td_m_msisdn a,(select distinct prov_code,area_code from td_m_msisdn where called_type = '1') b ");
	    parser.addSQL(" where 1=1 ");
	    parser.addSQL(" and :SERIAL_NUMBER between a.begin_msisdn and a.end_msisdn ");
	    parser.addSQL(" and a.asp='1' ");
	    parser.addSQL(" and a.area_code = b.area_code");
	    return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	}
	
	

}
