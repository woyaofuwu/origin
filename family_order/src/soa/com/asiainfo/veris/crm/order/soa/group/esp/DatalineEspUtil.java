package com.asiainfo.veris.crm.order.soa.group.esp;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.SubscribeViewInfoBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;

public class DatalineEspUtil {
	
	public static IData getEosInfo(IData map,String productId,String eparchyCode) throws Exception
    {
		 
        map.put("ATTR_CODE", "ESOP");
        map.put("ATTR_VALUE", map.getString("IBSYSID"));
        map.put("RSRV_STR1", map.getString("NODE_ID",""));
        map.put("RSRV_STR3", map.getString("SUB_IBSYSID",""));
        map.put("RSRV_STR4", map.getString("BUSIFORM_ID",""));
        map.put("RSRV_STR5", "NEWFLAG");//新老系统区分
        map.put("RSRV_STR6", map.getString("RECORD_NUM",""));//新老系统区分
        map.put("PRODUCT_ID", productId);
        // 根据产品ID 判断td_s_compare表，如果有数据，就填04 没有就填 01
        IData commParam = new DataMap();
        commParam.put("SUBSYS_CODE", "CSM");
        commParam.put("PARAM_ATTR", "3369");
        commParam.put("PARAM_CODE",productId);
        commParam.put("EPARCHY_CODE",eparchyCode);
        IDataset datasets = CSAppCall.call( "CS.CommparaInfoQrySVC.getCommpara", commParam);
        if (IDataUtil.isEmpty(datasets))
        {
        	map.put("RSRV_STR2", "01");
        }
        else
        {
        	map.put("RSRV_STR2", "04");
        }
		return map;
    }
	
	   /**
     * 查询必选服务和优惠
     * @param ElementsParam
     * @return
     * @throws Exception
     */
	public static IDataset getElementsInfo(IData ElementsParam) throws Exception{
    	
    	IDataset ElementsInfoList = CSAppCall.call("CS.SelectedElementSVC.getGrpUserOpenElements", ElementsParam);
        return ElementsInfoList;	
    }
	
	  /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfoFrontData(IData resultSetDataset) throws Exception
    {
    	IDataset productParamAttrset = new DatasetList();
    	Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttrset.add(productParamAttr);

        }
        
		return productParamAttrset;
    	
    }
    
    /**
     * 转换产品参数信息
     */
    public static IDataset saveProductParamInfo(IDataset productParamAttrset,IData resultSetDataset) throws Exception
    {
    	Iterator<String> iterator = resultSetDataset.keySet().iterator();
        while (iterator.hasNext())
        {
            IData productParamAttr = new DataMap();
            String key = iterator.next();
            Object value = resultSetDataset.get(key);
            productParamAttr.put("ATTR_CODE", key);
            productParamAttr.put("ATTR_VALUE", value);
            productParamAttrset.add(productParamAttr);

        }
        
		return productParamAttrset;
    	
    }
    
    /**
     * 获取开通归档时间
     */
    public static IData getStartDate(IData eos) throws Exception
    {
    	IData result = new DataMap();
    	String onceDiscnt  = "false";
    	String ibsysId = eos.getString("IBSYSID");
    	String recordNum = eos.getString("RECORD_NUM");
    	
    	IData rele = new DataMap();
    	IData detail = new DataMap();
    	IData accept = new DataMap();
    	
    	/*//查询流程关系表获取专线实例号
    	param.put("SUB_BUSIFORM_ID", busiformId);
    	IDataset reles = Dao.qryByCode("TF_B_EWE_RELE", "SEL_BY_SUBBUSIFORM", param, Route.getJourDb(Route.CONN_CRM_CG));
		if(IDataUtil.isNotEmpty(reles)){
			rele = reles.first();
		}*/
		//String productNo= rele.getString("RELE_VALUE");//专线实例号
		//通过专线实例号和ibsysId查询emos信息;rsrv_str6为专线实例号
		detail.put("RECORD_NUM", recordNum);
		detail.put("IBSYSID", ibsysId);
		detail.put("OPER_TYPE", "replyWorkSheet");
		IDataset details = Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_PRODUCTNO", detail,Route.getJourDb(Route.CONN_CRM_CG)) ;
		if(IDataUtil.isEmpty(details)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号"+ibsysId+"和专线RECORD_NUM"+recordNum+"查询TF_B_EOP_EOMS中replyWorkSheet无数据！");
		}
		rele = details.first();

		IData acceptParam = new DataMap();
		acceptParam.put("IBSYSID", ibsysId);
		acceptParam.put("ATTR_CODE", "ACCEPTTANCE_PERIOD");
		acceptParam.put("RECORD_NUM", "0");
		IDataset accepts = Dao.qryByCode("TF_B_EOP_OTHER", "SEL_BY_PERIOD", acceptParam,Route.getJourDb(Route.CONN_CRM_CG));
		if(IDataUtil.isNotEmpty(accepts)){
			accept = accepts.first();
		}
		int acceptDate = accept.getInt("ATTR_VALUE",0); //验收期暂未确定哪里取暂时写死
		String insertTime = rele.getString("INSERT_TIME");
		if(0 == acceptDate){
			if(Integer.parseInt(SysDateMgr.getStringDayByDate(insertTime)) < DataLineDiscntConst.CHARGEABLETIME){ //按当月
				insertTime = SysDateMgr.firstDayOfDate(insertTime,0)+ SysDateMgr.getFirstTime00000();
				//insertTime = SysDateMgr.firstDayOfDate(SysDateMgr.getSysDate(),acceptDate)+ SysDateMgr.getFirstTime00000();
			}else{
				insertTime = SysDateMgr.firstDayOfDate(insertTime,1)+ SysDateMgr.getFirstTime00000();//按下月生效
			//	insertTime = SysDateMgr.firstDayOfDate(insertTime,1+acceptDate)+ SysDateMgr.getFirstTime00000();
			}
			//insertTime ="";
		}else{
			onceDiscnt = "true";
			insertTime = SysDateMgr.getAddMonthsLastDay(acceptDate,insertTime);//按下月生效或下下个月生效
		}
		/*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse(insertTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int d = cal.get(Calendar.DATE);
		if(d<20){ //按当月
			insertTime = SysDateMgr.getAddMonthsLastDay(acceptDate,insertTime);
			//insertTime = SysDateMgr.firstDayOfDate(insertTime,1+acceptDate)+ SysDateMgr.getFirstTime00000();
		}else{
			insertTime = SysDateMgr.getAddMonthsLastDay(1+acceptDate,insertTime);
			//insertTime = SysDateMgr.firstDayOfDate(insertTime,2+acceptDate)+ SysDateMgr.getFirstTime00000();
		}*/
		result.put("INSERT_TIME", insertTime);
		result.put("ONCE_DISCNT", onceDiscnt);
		
    	return result;
    	
    }
    
    /**
     * 获取变更归档时间
     */
    public static String getChangeDate(IData eos) throws Exception
    {
    	String ibsysId = eos.getString("IBSYSID");
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysId);
    	param.put("NODE_ID", "apply");
    	param.put("ATTR_CODE", "ACCEPTTANCE_PERIOD");
    	param.put("RECORD_NUM", "0");
    	//根据ibsysid
    	int acceptDate = 0;
    	IDataset acceptTance = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_ATTRCODE_MAXSUB", param,Route.getJourDb(Route.CONN_CRM_CG)) ;
		if(DataUtils.isNotEmpty(acceptTance)){
			//CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询变更计费方式无数据！");
			acceptDate = acceptTance.first().getInt("ATTR_VALUE",0); //获取计费方式 0-立即生效；1-下账期生效；2-下下账期生效
		}
		String insertTime = "";
		if(0 == acceptDate){
			if(Integer.parseInt(SysDateMgr.getStringDayByDate(SysDateMgr.getSysDate())) < DataLineDiscntConst.CHARGEABLETIME){ //按当月
				insertTime = SysDateMgr.firstDayOfDate(SysDateMgr.getSysDate(),0)+ SysDateMgr.getFirstTime00000();
				//insertTime = SysDateMgr.firstDayOfDate(SysDateMgr.getSysDate(),acceptDate)+ SysDateMgr.getFirstTime00000();
			}else{
				insertTime = SysDateMgr.firstDayOfDate(SysDateMgr.getSysDate(),1)+ SysDateMgr.getFirstTime00000(); //按下个月生效
			//	insertTime = SysDateMgr.firstDayOfDate(insertTime,1+acceptDate)+ SysDateMgr.getFirstTime00000();
			}
		}else{
			insertTime = SysDateMgr.firstDayOfDate(SysDateMgr.getSysDate(),acceptDate)+ SysDateMgr.getFirstTime00000();
		}

		return insertTime;
    	
    }
    
    /**
   	 * 获取本省单条专线数据
   	 * 
   	 * @param reqParam
   	 * @return
   	 * @throws Exception
   	 */
   	 public static IDataset getDataLineInfo(IData reqParam) throws Exception{
   		 IDataset results= new DatasetList();
   		IData result = new DataMap();
   		
   		// 获取所有数据
   		IData param = new DataMap();
   		String recordNum = reqParam.getString("RECORD_NUM","");
   		String ibsysId = reqParam.getString("IBSYSID","");
   		String productId = reqParam.getString("PRODUCT_ID");
   		//String nodeId = reqParam.getString("NODE_ID");
   		param.put("IBSYSID", ibsysId);
   		//param.put("NODE_ID", nodeId);
   		if(!"7010".equals(productId)){
   			param.put("RECORD_NUM", recordNum);
   		}
   		IDataset eomsDatas = Dao.qryByCodeParser("TF_B_EOP_EOMS", "SEL_BY_IBSYSID_RECORDNUM_NODEID", param,Route.getJourDb(Route.CONN_CRM_CG));
	   	if(DataUtils.isEmpty(eomsDatas))
	    {
	     	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询EOMS表无数据！");
	    }
   		IData eomsData = eomsDatas.first(); 
   		IDataset commEomsSubData =  new DatasetList();
   		IDataset lineEomsSubData =  new DatasetList();
   		IDataset lineDataList = new DatasetList();
   		if("7010".equals(productId)){
   			
   			commEomsSubData = getEomsSubSimpleData(eomsData.getString("SUB_IBSYSID"),ibsysId,"0");
   			//查询专线信息，先查总共有多少条线
   			
   			IDataset eomsStates = Dao.qryByCodeParser("TF_B_EOP_EOMS_STATE", "SEL_BY_IBSYSID", param,Route.getJourDb(Route.CONN_CRM_CG));
   			
   			for(int i=0;i< eomsStates.size();i++){
   				IData eomsState =  eomsStates.getData(i);
   				String record = eomsState.getString("RECORD_NUM");
   				IData eomsParam  = new DataMap();
   				eomsParam.put("RECORD_NUM", record);
   				eomsParam.put("IBSYSID", ibsysId);
   				IDataset eomsInfos =  Dao.qryByCodeParser("TF_B_EOP_EOMS", "SEL_BY_IBSYSID_RECORDNUM_NODEID", eomsParam,Route.getJourDb(Route.CONN_CRM_CG));
   				IData eomsInfo  =  eomsInfos.first();
   				IDataset lineVoipEomsSubData =  getEomsSubSimpleDataLine(eomsInfo.getString("SUB_IBSYSID"),ibsysId,record,eomsInfo.getString("GROUP_SEQ"));
   				String paramName = getParamName(productId, eomsInfo.getString("SHEETTYPE"));
   				IData onLineInfo =  getLineDataListOne(lineVoipEomsSubData,paramName,productId);
   				eomsParam.put("ATTR_CODE", "ZJ");
   				IDataset zjInfos =  Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_IBSYSID_RECORDNUM_ATTRCODE", eomsParam,Route.getJourDb(Route.CONN_CRM_CG));
   				if(IDataUtil.isNotEmpty(zjInfos)){
   					onLineInfo.put("ZJ", zjInfos.first().getString("ATTR_VALUE"));
   				}
   				lineDataList.add(onLineInfo);
   			}
   			/*lineEomsSubData = getVoipData(ibsysId,"newWorkSheet");
   			if(IDataUtil.isEmpty(lineEomsSubData)){
   				lineEomsSubData = getVoipData(ibsysId,"newWorkSheet");
   			}*/
   		}else{
   			commEomsSubData = getEomsSubSimpleData(eomsData.getString("SUB_IBSYSID"),ibsysId,"0");

   			lineEomsSubData =  getEomsSubSimpleDataLine(eomsData.getString("SUB_IBSYSID"),ibsysId,recordNum,eomsData.getString("GROUP_SEQ"));

   		}
   		
   		String paramName = getParamName(productId, eomsData.getString("SHEETTYPE"));

   		if(!"7010".equals(productId)){
   	   		// 获取专线字段
   			lineDataList = getLineDataList(lineEomsSubData, paramName,productId);//
   		}
   		
   		// 获取公共字段
   		IData commDataMap = getCommonDataMap(eomsData, getFirstLine(commEomsSubData), paramName);	// SEL_ALLDLINE_BY_IBSYSID
   		
   		IData dlineData = new DataMap();
   		dlineData.put("COMM_DATA_MAP", commDataMap);
   		dlineData.put("LINE_DATA_LIST", lineDataList);
   		
   		result.put("DLINE_DATA", dlineData);
   		result.put("X_RSPCODE", "0");
   		result.put("X_RSPDESC", "success");
   		result.put("X_RESULTCODE", "0");
   		result.put("X_RESULTINFO", "success");
   		results.add(result);
   		return results;
   	}

   	/**
 	 * 获取单条业务数据
 	 * @param param
 	 * @return
 	 * @throws Exception
 	 */
 	private static IDataset getEomsSubSimpleData(String subIbsysid,String ibsysid,String recordNum) throws Exception {
 		
 		IData param = new DataMap();
 		param.put("SUB_IBSYSID", subIbsysid);
 		param.put("IBSYSID", ibsysid);
 		param.put("RECORD_NUM", recordNum);

 		//param.put("NODE_ID", nodeId);
 		//return Dao.qryByCodeParser("TF_B_EOP_EOMS_SUB", "SEL_SIMPLEDLINE_BY_IBSYSID" , param,Route.getJourDb(Route.CONN_CRM_CG));
 		IDataset result = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_INFO_BY_SUBIBSYSID" , param,Route.getJourDb(Route.CONN_CRM_CG));
 		if(DataUtils.isEmpty(result))
	    {
	     	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询发送资管的ATTR表无数据！");
	    }
 		return result;
 	}
 	
	/**
 	 * 获取单条业务数据
 	 * @param param
 	 * @return
 	 * @throws Exception
 	 */
 	private static IDataset getEomsSubSimpleDataLine(String subIbsysid,String ibsysid,String recordNum,String groupseq) throws Exception {
 		
 		IData param = new DataMap();
 		param.put("SUB_IBSYSID", subIbsysid);
 		param.put("IBSYSID", ibsysid);
 		param.put("RECORD_NUM", recordNum);
 		param.put("GROUP_SEQ", groupseq);
 		//param.put("NODE_ID", nodeId);
 		//return Dao.qryByCodeParser("TF_B_EOP_EOMS_SUB", "SEL_SIMPLEDLINE_BY_IBSYSID" , param,Route.getJourDb(Route.CONN_CRM_CG));
 		IDataset result = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_BY_EOMS_OPERTYPE" , param,Route.getJourDb(Route.CONN_CRM_CG));
 		if(DataUtils.isEmpty(result))
	    {
	     	CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询发送资管的ATTR表无数据！");
	    }
 		return result;
 	}
 	
	/**
 	 * 获取VOIP业务数据
 	 * @param param
 	 * @return
 	 * @throws Exception
 	 */
 	public static IDataset getVoipData(String ibsysid,String nodeId) throws Exception {
 		IData param = new DataMap();
 		//param.put("SUB_IBSYSID", subIbsysid);
 		param.put("IBSYSID", ibsysid);
 		param.put("NODE_ID", nodeId);
 		//return Dao.qryByCodeParser("TF_B_EOP_EOMS_SUB", "SEL_SIMPLEDLINE_BY_IBSYSID" , param,Route.getJourDb(Route.CONN_CRM_CG));
 		return Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_LINEINFO_BY_SUBIBSYSID" , param,Route.getJourDb(Route.CONN_CRM_CG));
 	}
 	
 	private static String getParamName(String productId, String sheetType) {
		String configName = "CONFIG_";
		if ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)) {
			configName += "DLINE";
		} else if ("7010".equals(productId)){
			configName += "VOIP";
		} else if ("7016".equals(productId)){
			configName += "IMS";
		}else {
			configName += "INET";
		}
		
		return configName + "_" + sheetType;
	}
 	
 	/**
	 * 获取专线信息
	 * @return
	 * @throws Exception 
	 */
	private static IDataset getLineDataList(IDataset eomsSubData, String configName,String productId) throws Exception {
		IDataset result = new DatasetList();
		
		//查询出公共信息所需字段	
		String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "PBOSS_DATA_LINE_CONFIG", configName});
		
		//String lineConf = ConfigManager.getInstance().getParamValue("PBOSS_DATA_LINE_CONFIG", configName);
		
		//匹配数据
		int remark = 1;	// 专线条数从1开始
		IData tempLine = new DataMap();
		for (int i = 0; i < eomsSubData.size(); i++) {
			IData data = eomsSubData.getData(i);
			if (remark != data.getInt("RECORD_NUM") && "7010".equals(productId)) {	// 不相等则是新专线
				remark = data.getInt("RECORD_NUM");
				result.add(tempLine);
				tempLine = new DataMap();
			}
			String fieldName = data.getString("ATTR_CODE", "").toUpperCase();
			if (lineConf.contains(fieldName + "|")) {
				
				if("BANDWIDTH".equals(fieldName)){	// crm: 带宽要换算成M且不带单位
					String valueStr = data.getString("ATTR_VALUE", "");
					if(valueStr.indexOf("M") != -1 || valueStr.indexOf("G") != -1){
						boolean flag = digitCheck(valueStr.substring(0, valueStr.length()-1));
						if(flag){
							if (valueStr.indexOf("G") != -1) {
								valueStr = valueStr.substring(0, valueStr.length()-1);
								valueStr += "000";
							} else {
								valueStr = valueStr.substring(0, valueStr.length()-1);
							}
						}
						else{
							valueStr = "0";
						}
					}
					tempLine.put(fieldName, valueStr);
				} else {
					tempLine.put(fieldName, data.getString("ATTR_VALUE", ""));
				}
				
			}
			
			
		}
		result.add(tempLine);	//最后一条专线
		
		return result;
	}
	
	/**
	 * 获取专线信息
	 * @return
	 * @throws Exception 
	 */
	private static IData getLineDataListOne(IDataset eomsSubData, String configName,String productId) throws Exception {
		
		//查询出公共信息所需字段	
		String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "PBOSS_DATA_LINE_CONFIG", configName});
		
		//String lineConf = ConfigManager.getInstance().getParamValue("PBOSS_DATA_LINE_CONFIG", configName);
		
		//匹配数据
		IData tempLine = new DataMap();
		for (int i = 0; i < eomsSubData.size(); i++) {
			IData data = eomsSubData.getData(i);
			String fieldName = data.getString("ATTR_CODE", "").toUpperCase();
			if (lineConf.contains(fieldName + "|")) {
				
				if("BANDWIDTH".equals(fieldName)){	// crm: 带宽要换算成M且不带单位
					String valueStr = data.getString("ATTR_VALUE", "");
					if(valueStr.indexOf("M") != -1 || valueStr.indexOf("G") != -1){
						boolean flag = digitCheck(valueStr.substring(0, valueStr.length()-1));
						if(flag){
							if (valueStr.indexOf("G") != -1) {
								valueStr = valueStr.substring(0, valueStr.length()-1);
								valueStr += "000";
							} else {
								valueStr = valueStr.substring(0, valueStr.length()-1);
							}
						}
						else{
							valueStr = "0";
						}
					}
					tempLine.put(fieldName, valueStr);
				} else {
					tempLine.put(fieldName, data.getString("ATTR_VALUE", ""));
				}
				
			}
			
			
		}
		return tempLine;
	}
	
	/**
	 * 纯数字校验
	 * @param input
	 * @return
	 */
	public static boolean digitCheck(String input) {    
		for(int i = 0; i < input.length(); i++) {        
			char c = input.charAt(i);        
			if( (c < '0' || c > '9') ) {       
				return false;        
			}    
		}    
		return true;
	}
	
	private static IData getFirstLine(IDataset allEomsSubData) throws Exception {
		IData result = new DataMap();
		for (int i = 0; i < allEomsSubData.size(); i++) {
			IData temp = allEomsSubData.getData(i);
			if ("0".equals(temp.getString("RECORD_NUM", "-1"))) {
				result.put(temp.getString("ATTR_CODE").toUpperCase(), temp.getString("ATTR_VALUE"));
			}
		}
		return result;
	}
	
	/**
	 * 获取本省专线公共信息
	 * @return
	 * @throws Exception 
	 */
	private static IData getCommonDataMap(IData eomsData, IData lineData, String paramName) throws Exception {
		IData result = new DataMap();
		String ibsysid = eomsData.getString("IBSYSID");
		
		//查询出公共信息所需字段	
		String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "PBOSS_COMMON_DATA_CONFIG", paramName});
		
		//String lineConf = ConfigManager.getInstance().getParamValue("PBOSS_COMMON_DATA_CONFIG", paramName);
		
		for (String fieldName : eomsData.getNames()) {
			if (lineConf.contains(fieldName)) {
				result.put(fieldName, eomsData.getString(fieldName, ""));
			}
		}
		
		for (String fieldName : lineData.getNames()) {
			if (lineConf.contains(fieldName)) {
				result.put(fieldName, lineData.getString(fieldName, ""));
			}
		}
		result.put("SERVICETYPE",eomsData.getString("SERVICETYPE"));
		//identifyDataLine(result, ibsysid);	// 区分两类传输专线
		result.put("PF_WAIT", "0");
		//getConfCRMTicketNo(result);	// 修改关联crm工单号
		transferFields(result);	// 字段转换配置
		
		//result.putAll(getStaffInfo(ibsysid));
		addSpecialInfo(result, ibsysid); 
		result.put("SUBSCRIBE_ID", ibsysid);
		
		return result;
	}
	
	private static void transferFields(IData result) throws Exception {
		
		String fieldName = "SERVICELEVEL";
		String confName = "servicelevel";

		String valueInEsop = result.getString(fieldName, "");
		if (!"".equals(valueInEsop)) {
			String value4Pboss = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{confName,valueInEsop});
			//String value4Pboss = ConfigManager.getInstance().getParamValue(confName, valueInEsop);
			result.put(fieldName, value4Pboss);
		}
		
	}
	
	//添加个性数据
		private static void addSpecialInfo(IData result, String ibsysid) throws Exception {
			String sql = "SELECT a.RSRV_STR4 TITLE,a.CUST_NAME,to_char(a.ACCEPT_TIME,'yyyy-mm-dd hh24:mi:ss') ACCEPT_TIME,a.GROUP_ID FROM TF_B_EOP_SUBSCRIBE a WHERE " +
					"a.IBSYSID=:IBSYSID";
			 
			IData param = new  DataMap();
			param.put("IBSYSID", ibsysid);
			SQLParser parser = new SQLParser(param);
		     parser.addSQL(sql);
			IData d =  Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG)).first();
			result.put("TITLE", d.getString("TITLE", "")); 
			if(!result.containsKey("CUSTOMER") || result.getString("CUSTOMER").equals(""))
				result.put("CUSTOMNAME", d.getString("CUST_NAME", "")); 
			if(!result.containsKey("CUSTOMNO") || result.getString("CUSTOMNO").equals(""))
				result.put("CUSTOMNO", d.getString("GROUP_ID", "")); 
			
			result.put("ACCEPT_TIME", d.getString("ACCEPT_TIME", ""));//工单创建时间
			//result.put("VIP_MANAGER_CODE", d.getString("VIP_MANAGER_CODE", ""));//客户经理信息
		}
	
		/**
		 * CRM获取业务信息，除开bboss业务的其他类业务
		 * @param reqParam
		 * @return
		 */
		public static IDataset doEsopInfo(IData reqParam) throws Exception{
			IDataset result = new DatasetList();
			String ibsysid = reqParam.getString("IBSYSID");
			IData map = new DataMap();
			map.put("IBSYSID", ibsysid);
			map.put("NODE_ID", "apply");
			//IData busi = ProductAttrBean.getProAttrByIbsysidNode(ibsysid,nodeId);
			//IData busi = ProductAttrBean.getProAttrByIbsysid(ibsysid);
			IDataset busis = Dao.qryByCodeParser("TF_B_EOP_ATTR", "SEL_ATTRINFO_BY_IBSYSID", map, Route.getJourDb(Route.CONN_CRM_CG));
			IData busi = saveProductParamInfoFrontDataset(busis);
			//AttrMap pattr = new AttrMap();
			//pattr.buiderCol2RowDefault("TF_B_WORKFORM_PRODUCT_ATTR", new String[]{"IBSYSID"}, new String[]{ibsysid});
			//IDataset set = pattr.getMultiterm(AttrMap.NO_PARENT);
			//busi.put("CLONE_SOURCE", set);
			//data.putAll(busi);
			result.add(busi);
			return result;
		}
		
		public static IData getAcctInfo(String ibsysid) throws Exception{
			IData result = new DataMap();
			IDataset acctInfos = SubscribeViewInfoBean.qryEweAttributesByNodeIdIbsysid(ibsysid,"apply",null);
			if(DataUtils.isEmpty(acctInfos))
	        {
	        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据该订单号查询无业务数据！");
	        }
			IData acctInfo = saveProductParamInfoFrontDataset(acctInfos);
			
			//查询出公共信息所需字段	
			String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[]{ "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[]{ "PBOSS_ACCT_INFO_CONFIG", "ACCT_INFO"});
			for (String fieldName : acctInfo.getNames()) {
				if (lineConf.contains(fieldName)) {
					result.put(fieldName, acctInfo.getString(fieldName, ""));
				}
			}
			return result;
			
		}
		
		
    public static IData getCommInfo(String ibsysid) throws Exception {
        IData result = new DataMap();
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("NODE_ID", "apply");
        param.put("RECORD_NUM", "0");
        
        IDataset commInfos = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(param);
        if(DataUtils.isEmpty(commInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据该订单号查询无业务数据！");
        }
        IData commInfo = saveProductParamInfoFrontDataset(commInfos);

        //查询出公共信息所需字段	
        String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "PBOSS_COMM_INFO_CONFIG", "COMM_INFO" });
        for (String fieldName : commInfo.getNames()) {
            if(lineConf.contains(fieldName)) {
                result.put(fieldName, commInfo.getString(fieldName, ""));
			}
		}
        return result;

    }

		  /**
	     * 转换产品参数信息
	     */
	    public static IData saveProductParamInfoFrontDataset(IDataset resultSetDataset) throws Exception
	    {
	    	IData productParamAttrset = new DataMap();
	    	for(int i=0;i < resultSetDataset.size();i++){
	    		IData result = resultSetDataset.getData(i);
	    		String attrCode = result.getString("ATTR_CODE");
	    		String attrValue = result.getString("ATTR_VALUE");
	    		productParamAttrset.put(attrCode, attrValue);
	    	}
			return productParamAttrset;
	    	
	    }
	    
	    /**
	     * 根据用户UserId查询
	     */
	    public static IData getElementInfo(String userId,String discntId) throws Exception
	    {
	    	//根据userId和elementId查询用户资费
	    	IDataset  discntInfos  = UserDiscntInfoQry.queryUserDiscntV(userId,discntId);
	    	if(DataUtils.isEmpty(discntInfos))
	        {
	        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据用户"+userId+"查询用户无优惠信息！");
	        }
	    	String instId  = discntInfos.first().getString("INST_ID");
	    	IData input =  new DataMap();
	    	input.put("USER_ID", userId);
	    	input.put("RELA_INST_ID", instId);
	    	IDataset results = Dao.qryByCode("TF_F_USER_ATTR", "SEL_ALL_EXIST_MEMBER", input, Route.CONN_CRM_CG);
	    	if(DataUtils.isEmpty(results))
	        {
	        	 CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据用户"+userId+"查询用户无优惠信息！");
	        }
	    	IData result = saveProductParamInfoFrontDataset(results);
	    	result.put("START_DATE", results.getData(0).getString("START_DATE"));
			return result;
	    
	    }
	    
	    /**
	     * 查询一次性费用是否计费
	     */
	    public static IData getOnceDiscntAttr(String endData,String userId,String productId) throws Exception
	    {
	    	IData result =  new DataMap();
	    	//根据userId查询一次性优惠
	    	IData discntOnce  = new DataMap();
	    	IDataset discntInfos = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId,DataLineDiscntConst.disposableElementId,Route.CONN_CRM_CG);
	    	if(IDataUtil.isNotEmpty(discntInfos)){
	    		discntOnce = discntInfos.first();
	    	}
	    	String discntCode = "";
	    	if("97011".equals(productId)){
	    		discntCode = DataLineDiscntConst.getElementIdToProductId("7011",userId);
	    	}else if("97012".equals(productId)){
	    		discntCode = DataLineDiscntConst.getElementIdToProductId("7012",userId);
	    	}else if("97016".equals(productId)){
	    		discntCode = DataLineDiscntConst.imsElementId;
	    	}else if("970111".equals(productId)){
	    		discntCode = DataLineDiscntConst.internet1ElementId;
	    	}else if("970112".equals(productId)){
	    		discntCode = DataLineDiscntConst.internet2ElementId;
	    	}else if("970121".equals(productId)){
	    		discntCode = DataLineDiscntConst.dataline1ElementId;
	    	}else if("970122".equals(productId)){
	    		discntCode = DataLineDiscntConst.dataline2ElementId;
	    	}
	    	String startData  =  "";
	    	IDataset discntAttr = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId,discntCode,Route.CONN_CRM_CG);
	    	if(IDataUtil.isNotEmpty(discntAttr)){
	    		startData = discntAttr.first().getString("START_DATE");
	    	}
	    	if(IDataUtil.isNotEmpty(discntOnce)){
	    		endData = SysDateMgr.decodeTimestamp(endData, SysDateMgr.PATTERN_TIME_YYYYMM);
	    		startData  = SysDateMgr.decodeTimestamp(discntOnce.getString("END_DATE"), SysDateMgr.PATTERN_TIME_YYYYMM);
	    		if(Double.parseDouble(endData) > Double.parseDouble(startData)+1){
	    			result.put("FLAG", true);
	    			
	    			return result;
	    		}
	    		result.put("START_DATE", SysDateMgr.firstDayOfDate(discntOnce.getString("END_DATE"),1));
	    		result.put("FLAG", false);
	    		return result;
	    	}else{
	    		IData param = new DataMap();
            	param.put("DISCNT_CODE", discntCode);
            	param.put("USER_ID", userId);
            	IDataset dscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_ORDERDISCNT_ALL", param);
            	if(IDataUtil.isNotEmpty(dscnts))
            	{
            		startData = SysDateMgr.decodeTimestamp(dscnts.first().getString("START_DATE"), SysDateMgr.PATTERN_TIME_YYYYMM);
            		endData = SysDateMgr.decodeTimestamp(endData, SysDateMgr.PATTERN_TIME_YYYYMM);
            		if(Integer.parseInt(endData) >  Integer.parseInt(startData)){
            			result.put("FLAG", true);
    	    			return result;
    	    		}
            		result.put("START_DATE", SysDateMgr.firstDayOfDate(dscnts.first().getString("START_DATE"),0));
            		result.put("FLAG", false);
    	    		return result;
            	}
	    	}
			return null;
	    
	    }
	    
}
