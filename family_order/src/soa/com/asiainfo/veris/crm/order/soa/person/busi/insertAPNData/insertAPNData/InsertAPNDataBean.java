
package com.asiainfo.veris.crm.order.soa.person.busi.insertAPNData.insertAPNData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class InsertAPNDataBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(InsertAPNDataBean.class);
  
	/*
	 * 情景一：将物联网APN数据截止  
		1、导入按钮：前线人员整理好三字段，PARA_CODE1（APN名称）、end_time（格式为年月日，例如20180801）、remark（该字段填写业务需求工单编号），界面支持输入框输入或批量导入这两字段（考虑excel兼容性问题，建议支持模板下载功能）  
		2、检验按钮：根据关键字PARA_CODE1在表ucr_cen1.td_s_commpara检索，看数据是否是存在唯一大于end_time的数据；满足条件，才能校验通过才能继续办理； 
		3、 查询条件是ucr_cen1.td_s_commpara a  where a.subsys_code='CSM' and a.partm_attr='3995'；
	 */
	public IData verifyInfo(IData param)throws Exception{
		IData rtnData=new DataMap();
		String paraCode1=param.getString("PARA_CODE1","");
		//String endDate=param.getString("END_DATE","");
		String sysTime = SysDateMgr.getSysTime();
	 
		IData paramSQL = new DataMap();
        IDataset queryResult = new DatasetList();
        paramSQL.put("PARA_CODE1", paraCode1);
        paramSQL.put("END_DATE", sysTime);
        //paramSQL.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		try{
		 	
	        SQLParser parser = new SQLParser(paramSQL);
	        // 查询是否已经存在
	        parser.addSQL("SELECT PARA_CODE1,END_DATE,REMARK ");
	        parser.addSQL("FROM TD_S_COMMPARA C ");
	        parser.addSQL("WHERE C.PARA_CODE1 =:PARA_CODE1 ");
	        parser.addSQL("AND C.END_DATE > to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");

	        rtnData.put("PARA_CODE1", paraCode1);
	        queryResult = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	        if ( CollectionUtils.isEmpty(queryResult) ) {
	        	//插入数据
	        	IData insertResult = this.insertCommpara(param);
	        	String ins_count = insertResult.getString("INS_COUNT","");
	        	if( "1".equals(ins_count) ){
	        		rtnData.put("RESULT_CODE", "1");
					rtnData.put("RESULT_INFO", "成功！");
					//rtnData.put("RESULT_DATA", queryResult);
	        	}else{
	        		rtnData.put("RESULT_CODE", "-1");
					rtnData.put("RESULT_INFO", "入库失败！");
					logger.info("APN数据插入入库失败：ins_count:"+ins_count+";PARA_CODE1:"+paraCode1+";查询出数据size:"+queryResult.size());
	        	}
			}else {
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "数据已存在！");
				logger.info("APN数据插入失败：数据已存在！"+";PARA_CODE1:"+paraCode1+";查询出数据size:"+queryResult.size());
			}
			
		}catch(Exception e){
	    	String error =  Utility.parseExceptionMessage(e);
	    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strExceptionMessage = errorArray[1];
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "检索【"+paraCode1+"】失败:"+strExceptionMessage);
				logger.info("APN数据插入异常！"+";PARA_CODE1:"+paraCode1+";strExceptionMessage:"+strExceptionMessage);
			}
			else
			{
				rtnData.put("RESULT_CODE", "-1");
				rtnData.put("RESULT_INFO", "检索【"+paraCode1+"】失败:"+error);
				logger.info("APN数据插入异常！"+";PARA_CODE1:"+paraCode1+";error:"+error);
			}  
         }
		return rtnData;
	}
	
	public IData insertCommpara(IData param)throws Exception{
		 
		IData baseParam = new DataMap();
		IDataset queryResult = new DatasetList();
		IData comParam = new DataMap();
		String sysTime = SysDateMgr.getSysTime();
		baseParam.put("SUBSYS_CODE", "CSM");
		baseParam.put("PARAM_ATTR", "3995");
		baseParam.put("END_DATE", sysTime);
		SQLParser baseParser = new SQLParser(baseParam);
		// 查询基本信息
		baseParser.addSQL("SELECT PARA_CODE1,END_DATE,REMARK,PARAM_CODE,PARAM_NAME,PARA_CODE2,PARA_CODE3," +
				"PARA_CODE4,PARA_CODE23,PARA_CODE24,PARA_CODE25,PARA_CODE22,PARA_CODE21,EPARCHY_CODE ");
		baseParser.addSQL("FROM TD_S_COMMPARA C ");
		baseParser.addSQL("WHERE C.SUBSYS_CODE =:SUBSYS_CODE ");
		baseParser.addSQL("AND C.PARAM_ATTR =:PARAM_ATTR ");
		baseParser.addSQL("AND C.END_DATE > to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss') ");
		queryResult = Dao.qryByParse(baseParser, Route.CONN_CRM_CEN);
	    if ( !CollectionUtils.isEmpty(queryResult) ) {
	    	comParam.put("PARAM_CODE", ((IData) queryResult.get(0)).getString("PARAM_CODE","0"));
	    	comParam.put("PARAM_NAME", ((IData) queryResult.get(0)).getString("PARAM_NAME",""));
	    	comParam.put("PARA_CODE2", ((IData) queryResult.get(0)).getString("PARA_CODE2",""));
	    	comParam.put("PARA_CODE3", ((IData) queryResult.get(0)).getString("PARA_CODE3",""));
	    	comParam.put("PARA_CODE4", ((IData) queryResult.get(0)).getString("PARA_CODE4",""));
	    	comParam.put("PARA_CODE23", ((IData) queryResult.get(0)).getString("PARA_CODE23",""));
			comParam.put("PARA_CODE24", ((IData) queryResult.get(0)).getString("PARA_CODE24",""));
			comParam.put("PARA_CODE25", ((IData) queryResult.get(0)).getString("PARA_CODE25",""));
			comParam.put("PARA_CODE22", ((IData) queryResult.get(0)).getString("PARA_CODE22",""));
			comParam.put("PARA_CODE21", ((IData) queryResult.get(0)).getString("PARA_CODE21",""));
			comParam.put("EPARCHY_CODE", ((IData) queryResult.get(0)).getString("EPARCHY_CODE",""));
	    }else{
	    	comParam.put("PARAM_CODE", "0");
	    	comParam.put("EPARCHY_CODE", "ZZZZ");
	    }
	        
		IData result = new DataMap();
		String paraCode1=param.getString("PARA_CODE1","");
		String reamrk=param.getString("REMARK","");
		//String endDate=param.getString("END_DATE","");
		
		/*String offerCode=param.getString("OFFER_CODE","");
		String offerType=param.getString("OFFER_TYPE","");
		String modifyTag=param.getString("MODIFY_TAG","");
		String template=param.getString("TEMPLATE_CONTENT","");
		
		IData variableData = new DataMap();
		variableData.put("OFFER_CODE", offerCode);
		variableData.put("OFFER_TYPE", offerType);
		variableData.put("MODIFY_TAG", modifyTag);
		
		template = transformAndcheckContent(template, variableData);

		String cancelTag = param.getString("CANCEL_TAG", "0");*/
		//String endTime = SysDateMgr.addSecond(sysTime, -1);

		
		comParam.put("PARAM_ATTR", "3995");
		comParam.put("SUBSYS_CODE", "CSM");
		comParam.put("PARA_CODE1", paraCode1);
		comParam.put("START_DATE", sysTime);
		comParam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		//comParam.put("EPARCHY_CODE", "ZZZZ");
		comParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		comParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		comParam.put("UPDATE_TIME", SysDateMgr.getSysDate());
		comParam.put("REMARK", reamrk);
		
		if (Dao.insert("TD_S_COMMPARA", comParam, Route.CONN_CRM_CEN))
		{
			result.put("INS_COUNT", 1);
		}
		
		/*IData paramSql = new DataMap();
		paramSql.put("OFFER_CODE", offerCode);
		paramSql.put("OFFER_TYPE", offerType);
		paramSql.put("CANCEL_TAG", cancelTag);
		paramSql.put("MODIFY_TAG", modifyTag);
		paramSql.put("START_DATE", sysTime);
		paramSql.put("END_DATE", endTime);

		paramSql.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		paramSql.put("UPDATE_DEPART_ID", param.getString("ORG_ID", ""));
		paramSql.put("UPDATE_STAFF_ID", param.getString("OP_ID", ""));

		int charLength = SmsSend.getCharLength(template, 500);// 截取字符位置
		int i = 1;
		while (charLength < template.length() && i <= 5)// 需要截取
		{
			String template1 = template.substring(0, charLength);
			paramSql.put("TEMPLATE_CONTENT" + i++, template1);
			template = template.substring(charLength);
			charLength = SmsSend.getCharLength(template, 500);// 截取字符位置
		}
		if (i > 5)
		{
			CSAppException.appError("-1", "模版内容过长！");
		}
		paramSql.put("TEMPLATE_CONTENT" + i, template);
		//Dao.insert("TD_B_UPC_RECEIPT_TEMPLATE", param, Route.CONN_CRM_CEN);
		
		//产品或者资费入commparam表，由CHANGE_DISCNT MVEL解析
		
		IData comParam = new DataMap();
		String paramAttr ="1980";
		
		IDataset comparaInfo = CommparaInfoQry.getCommparaInfoByCode2("CSM", paramAttr, offerCode ,offerType, "ZZZZ");
		if(IDataUtil.isNotEmpty(comparaInfo)){
			IData endParam  =  new DataMap();
			endParam.put("PARAM_ATTR", paramAttr);
			endParam.put("PARAM_CODE", offerCode);
			endParam.put("PARA_CODE1", modifyTag);
			endParam.put("PARA_CODE2", offerType);
			endParam.put("END_DATE", sysTime);
			
			SQLParser sql = new SQLParser(endParam);
			sql.addSQL("UPDATE TD_S_COMMPARA T");
			sql.addSQL(" set END_DATE=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),");
			sql.addSQL(" REMARK='一级产商品落地的免填单信息，先终止已经存在的'");
			sql.addSQL(" WHERE SUBSYS_CODE='CSM'");
			sql.addSQL(" AND T.PARAM_ATTR=:PARAM_ATTR");
			sql.addSQL(" AND T.PARAM_CODE=:PARAM_CODE");
			sql.addSQL(" AND T.PARA_CODE1=:PARA_CODE1");
			sql.addSQL(" AND T.PARA_CODE2=:PARA_CODE2");
			sql.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE");
			int enSuc = Dao.executeUpdate(sql, Route.CONN_CRM_CEN);
			//result.put("END_COUNT", enSuc);
		}
		
		if(StringUtils.isNotEmpty(template))
		{
			comParam.put("PARAM_ATTR", paramAttr);
			comParam.put("SUBSYS_CODE", "CSM");
			comParam.put("PARAM_CODE", offerCode);
			comParam.put("PARAM_NAME", offerCode);
			comParam.put("PARA_CODE1", paraCode1);
			comParam.put("PARA_CODE2", offerType);
			comParam.put("PARA_CODE3", "");
			comParam.put("PARA_CODE4", "UPC");
			comParam.put("START_DATE", sysTime);
			comParam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
			comParam.put("EPARCHY_CODE", "ZZZZ");
			comParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			comParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			comParam.put("UPDATE_TIME", SysDateMgr.getSysDate());
			comParam.put("REMARK", reamrk);
			
			if (Dao.insert("TD_S_COMMPARA", comParam, Route.CONN_CRM_CEN))
			{
				//result.put("INS_COUNT", 1);
			}
		}
		*/	
		
		
		return result;
		
	}
	 
 
 
	 
    
	 
    
    
}
