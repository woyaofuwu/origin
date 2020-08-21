package com.asiainfo.veris.crm.order.soa.person.busi.cmonline;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ComFuncUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;

public class SmsBoomProtectionSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	private static transient final Logger log = Logger.getLogger(SmsBoomProtectionSVC.class);
	
	public IData C898HQSyncProtectedSMSBombList(IData inParamStr)throws Exception{
		IData inParam = new DataMap(inParamStr.toString());
		//入参非空判断
		IDataset dataset = new DatasetList();
		IData dataMap1 = new DataMap();
		dataMap1.put("FIELD", "contactChannel");
		dataMap1.put("TYPE", "String");
		dataset.add(dataMap1);
		
		IData dataMap2 = new DataMap();
		dataMap2.put("FIELD", "acceptNumber");
		dataMap2.put("TYPE", "String");
		dataset.add(dataMap2);
		
		IData dataMap3 = new DataMap();
		dataMap3.put("FIELD", "acceptTime");
		dataMap3.put("TYPE", "String");
		dataset.add(dataMap3);
		
		IData dataMap4 = new DataMap();
		dataMap1.put("FIELD", "province");
		dataMap1.put("TYPE", "String");
		dataset.add(dataMap4);
		
		IData dataMap5 = new DataMap();
		dataMap2.put("FIELD", "subNumber");
		dataMap2.put("TYPE", "String");
		dataset.add(dataMap5);
		
		IData dataMap6 = new DataMap();
		dataMap3.put("FIELD", "effectiveTime");
		dataMap3.put("TYPE", "String");
		dataset.add(dataMap6);
		
		IData dataMap7 = new DataMap();
		dataMap3.put("FIELD", "updateType");
		dataMap3.put("TYPE", "String");
		dataset.add(dataMap7);
		
		IData inParamResult = ComFuncUtil.checkInParam(inParam.getData("params"), dataset);
		if (!ComFuncUtil.CODE_RIGHT.equals(inParamResult.getString("respCode"))) {
			return inParamResult;
		}
		
		String updateType = inParam.getData("params").getString("updateType");
		IData result = new DataMap();
		if("1".equals(updateType)){
			result = addSmsBoomProtection(inParam);//新增
		}else if("2".equals(updateType)){
			result = delSmsBoomProtection(inParam);//删除
		}else if("3".equals(updateType)){
			result = updSmsBoomProtection(inParam);//修改
		}
		return result;
	}
	
	public IData addSmsBoomProtection(IData inParamStr)throws Exception{
		
		IData inParam = inParamStr.getData("params");
		String contactChannel = inParam.getString("contactChannel");
		String acceptNumber = inParam.getString("acceptNumber");//受理号码
		String acceptTime = inParam.getString("acceptTime");
		String province = inParam.getString("province");
		String subNumber = inParam.getString("subNumber");//被保护号码
		
		String effectiveTime = inParam.getString("effectiveTime");//生效截止时间
		String updateType = inParam.getString("updateType");
		String oneyearlatedate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		String oneyearlatetime = DateFormatUtils.format(SysDateMgr.string2Date(oneyearlatedate, "yyyy-MM-dd"), "yyyyMMdd")+"235959";
		
		int oneyearlatetag = effectiveTime.compareTo(oneyearlatetime);
		String nowdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		int tag = effectiveTime.compareTo(nowdate);
	    if(tag<0){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
	     }else if(oneyearlatetag>0){//时间小于一年
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
	  	}
		
	    IData insertData = new DataMap();
	    insertData.put("EXPIRE_DATE",effectiveTime);
    	
	    insertData.put("SERIAL_NO",acceptNumber);//被保护号码
	    insertData.put("ACCESS_NO", subNumber);//受理号码(老代码逻辑中，受理号码和被保护号码入表对调)
	    insertData.put("SERIAL_NUMBER",acceptNumber);
	    IDataset infos = CSAppCall.call("SS.SmsBoomProtectionSVC.qryProtectinfoByAccessNum", insertData);
	    if(IDataUtil.isNotEmpty(infos)){
	    	CSAppException.apperr(CrmCommException.CRM_COMM_103, "开通失败，该用户已存在！");
		}
	    insertData.put("CREATE_STAFF_ID", getVisit().getStaffId());//受理员工工号
	    insertData.put("ACCEPT_TIME",acceptTime);//受理时间
	    insertData.put("STATUS","0");//名单状态
	    insertData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
	    insertData.put("SMSBOMB_BUSINESS_TYPE",updateType);// 更新方式  
		insertData.put("CHANNEL_ID", contactChannel);//接触渠道编码.
	    
		IData resultMap = new DataMap();
	 	IData returnMap = new DataMap();
		try{
			infos = CSAppCall.call("SS.SmsBoomProtectionSVC.insertdateProtectInfo", insertData);
		}catch (Exception e) {
			resultMap.put("respCode", ComFuncUtil.CODE_ERROR);
			resultMap.put("respDesc", ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
			returnMap.put("object", resultMap);
			returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
		 	returnMap.put("rtnMsg", "失败");
			return returnMap;
		}
	 	
	 	resultMap.put("respCode", "0");
	 	resultMap.put("respDesc", "success");
	 	returnMap.put("object", resultMap);
	 	returnMap.put("rtnCode", "0");
	 	returnMap.put("rtnMsg", "成功");
		return returnMap;
	}
	
	public IData delSmsBoomProtection(IData inParamStr)throws Exception{
		
		IData inParam = inParamStr.getData("params");
		String contactChannel = inParam.getString("contactChannel");
		String acceptNumber = inParam.getString("acceptNumber");//受理号码
		String acceptTime = inParam.getString("acceptTime");
		String province = inParam.getString("province");
		String subNumber = inParam.getString("subNumber");//被保护号码
		
		String effectiveTime = inParam.getString("effectiveTime");//生效截止时间
		String updateType = inParam.getString("updateType");
		
		IData delData = new DataMap();
		delData.put("SERIAL_NO",acceptNumber);
		delData.put("ACCESS_NO",subNumber);
		delData.put("SERIAL_NUMBER",acceptNumber);

	    IDataset infos = CSAppCall.call("SS.SmsBoomProtectionSVC.qryProtectinfo", delData);
        if(IDataUtil.isEmpty(infos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "操作失败，该用户不存在！");
        }
	   
        IData result = infos.getData(0);
        delData.put("CHANNEL_ID", contactChannel);// 渠道编码
        delData.put("PROV_ID", province);//被保护省ID
        delData.put("ACCEPT_TIME", acceptTime);//受理时间
        delData.put("EXPIRE_DATE", effectiveTime);//截止时间
        delData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
        delData.put("CREATE_STAFF_ID", result.getString("CREATE_STAFF_ID"));// 受理员工工号
        delData.put("SMSBOMB_BUSINESS_TYPE", updateType);// 更新方式

        IData resultMap = new DataMap();
	 	IData returnMap = new DataMap();
        try{
        	infos = CSAppCall.call("SS.SmsBoomProtectionSVC.delProtectInfo", delData);
        }catch (Exception e) {
        	resultMap.put("respCode", ComFuncUtil.CODE_ERROR);
        	resultMap.put("respDesc", ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
			returnMap.put("object", resultMap);
			returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
		 	returnMap.put("rtnMsg", "失败");
			return returnMap;
		}
		
        resultMap.put("respCode", "0");
	 	resultMap.put("respDesc", "success");
	 	returnMap.put("object", resultMap);
	 	returnMap.put("rtnCode", "0");
	 	returnMap.put("rtnMsg", "成功");
		return returnMap;
	}
	
	public IData updSmsBoomProtection(IData inParamStr)throws Exception{
		
		IData inParam = inParamStr.getData("params");
		String contactChannel = inParam.getString("contactChannel");
		String acceptNumber = inParam.getString("acceptNumber");//受理号码
		String acceptTime = inParam.getString("acceptTime");
		String province = inParam.getString("province");
		String subNumber = inParam.getString("subNumber");//被保护号码
		
		String effectiveTime = inParam.getString("effectiveTime");//生效截止时间
		String updateType = inParam.getString("updateType");
		String oneyearlatedate = SysDateMgr.addYears(SysDateMgr.getSysDate("yyyy-MM-dd"), 1);
		String oneyearlatetime = DateFormatUtils.format(SysDateMgr.string2Date(oneyearlatedate, "yyyy-MM-dd"), "yyyyMMddHHmmss");
		
		int oneyearlatetag = effectiveTime.compareTo(oneyearlatetime);
		String nowdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		int tag = effectiveTime.compareTo(nowdate);
	    if(tag<0){
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能小于当前时间！");
	     }else if(oneyearlatetag>0){//时间小于一年
	    	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "生效截止时间不能超过当前时间一年！");
	  	}
		
		IData updData = new DataMap();
		updData.put("SERIAL_NO",acceptNumber);
		updData.put("ACCESS_NO", subNumber);
		updData.put("SERIAL_NUMBER",acceptNumber);
		IDataset infos = CSAppCall.call("SS.SmsBoomProtectionSVC.qryProtectinfo", updData);
        if(IDataUtil.isEmpty(infos)){
       	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "操作失败，该用户不存在！");
        }
        IData result = infos.getData(0);
        updData.put("EXPIRE_DATE",effectiveTime);//生效截止时间
        updData.put("CHANNEL_ID", contactChannel);// 渠道编码
        updData.put("PROV_ID", result.getString("PROV_ID"));//被保护省ID
        updData.put("ACCEPT_TIME", result.getString("ACCEPT_TIME"));//受理时间
        updData.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());// 更新时间
        updData.put("CREATE_STAFF_ID", result.getString("CREATE_STAFF_ID"));// 受理员工工号
        updData.put("SMSBOMB_BUSINESS_TYPE",updateType);// 更新方式
        
        IData resultMap = new DataMap();
	 	IData returnMap = new DataMap();
        try{
        	infos = CSAppCall.call("SS.SmsBoomProtectionSVC.updateProtectInfo", updData);
        }catch (Exception e) {
        	resultMap.put("respCode", ComFuncUtil.CODE_ERROR);
        	resultMap.put("respDesc", ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
			returnMap.put("object", resultMap);
			returnMap.put("rtnCode", ComFuncUtil.CODE_ERROR);
		 	returnMap.put("rtnMsg", "失败");
			return returnMap;
		}
        
	 	resultMap.put("respCode", "0");
	 	resultMap.put("respDesc", "success");
	 	returnMap.put("object", resultMap);
	 	returnMap.put("rtnCode", "0");
	 	returnMap.put("rtnMsg", "成功");
		return returnMap;
	}
}
