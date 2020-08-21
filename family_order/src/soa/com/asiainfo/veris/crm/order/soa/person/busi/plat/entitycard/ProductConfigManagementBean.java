package com.asiainfo.veris.crm.order.soa.person.busi.plat.entitycard;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class ProductConfigManagementBean extends CSBizBean{

	public static IDataset queryProductConfigInfo(IData inparam, Pagination pagination) throws Exception{
    	String fileName = inparam.getString("FILE_NAME", "");
    	String productModelId = inparam.getString("PRODUCT_MODEL_ID", "");
    	String operCode = inparam.getString("OPER_CODE", "");
    	String dealFlag = inparam.getString("DEAL_FLAG", "");
    	StringBuilder sql = new StringBuilder();
    	IData param = new DataMap();
    	sql.append("SELECT DATA_TYPE,FILE_NAME,OPRNUMB,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,FIELD6,FIELD7,FIELD8,");
    	sql.append("FIELD9,FIELD10,FIELD11,FIELD12,FIELD13,FIELD14,FIELD15,FIELD16,FIELD17,FIELD18,FIELD19,FIELD20,");
    	sql.append("INSERT_TIME,DEAL_FLAG,DEAL_TIME,RESULT_CODE,RESULT_INFO,PROC_ID,OPER_TYPE,VALID_DATE,EXPIRE_DATE,TIME_STAMP");
    	sql.append(" FROM TI_O_DATAIMPORT_DETAIL WHERE DATA_TYPE='VSCPSYN'") ; 
    	if(null!= fileName && !"".equals(fileName))
    	{
    		sql.append(" AND FILE_NAME=:FILE_NAME "); 
    		param.put("FILE_NAME", fileName);
    	}
    	if(null!= productModelId && !"".equals(productModelId))
    	{
    		sql.append(" AND FIELD5=:FIELD5 "); 
    		param.put("FIELD5", productModelId);
    	}
    	if(null!= operCode && !"".equals(operCode))
    	{
    		sql.append(" AND FIELD6=:FIELD6 "); 
    		param.put("FIELD6", operCode);
    	}
    	if(null!= dealFlag && !"".equals(dealFlag))
    	{
    		sql.append(" AND DEAL_FLAG=:DEAL_FLAG "); 
    		param.put("DEAL_FLAG", dealFlag);
    	}
    	sql.append(" ORDER BY INSERT_TIME DESC ") ; 
    	IDataset results = Dao.qryBySql(sql,param ,pagination, Route.CONN_CRM_CEN); 
		return results;
	}
	
	public boolean CallIBossHttpForFeedback(IData date) throws Exception{
		IData inData= new DataMap();
		IData param= new DataMap();
		IData product= new DataMap();
		IDataset productList= new DatasetList();
	  	String datatype = date.getString("DATA_TYPE");
    	String oprnumb = date.getString("OPRNUMB");
    	String filename = date.getString("FILE_NAME");
    	param.put("DATA_TYPE", datatype);
    	param.put("OPRNUMB", oprnumb);
    	param.put("FILE_NAME", filename);
    	IData synInfo = this.querySynProductInfo(param);
    	
		inData.put("KIND_ID","BIP2B297_T2001097_0_0");//交易唯一标识
		inData.put("ROUTETYPE", "00");
		inData.put("ROUTEVALUE", "000");
		inData.put("PROV_CODE", this.getProvCode());
		inData.put("PKG_SEQ", this.getProvCode()+SeqMgr.getPkgSeqId());  //交易包流水号
		//产品列表构造
		product.put("PRODUCT_ID", synInfo.getString("FIELD3"));
		product.put("PRODUCT_NAME", synInfo.getString("FIELD4"));
		product.put("ORDER_RESULT", "0");   //业务订购处理结果 
		product.put("FAIL_REASON", "");
		product.put("SEQ", synInfo.getString("FILE_NAME"));
		productList.add(product);
		inData.put("PRODUCT_LIST", productList);
		IDataset result = IBossCall.callHttpIBOSS("IBOSS", inData);
	    if ((result == null))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        }
        if (result.isEmpty())
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (result.size() < 1)
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (!"0000".equals(result.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RSPDESC", ""));
        }
		if(result!=null && "0".equals(result.getData(0).getString("X_RESULTCODE","")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean CallIBossHttpForExtension(IData date) throws Exception{
		IData inData= new DataMap();
		IData param= new DataMap();
		IData product= new DataMap();
		IDataset productList= new DatasetList();
	  	String datatype = date.getString("DATA_TYPE");
    	String oprnumb = date.getString("OPRNUMB");
    	String filename = date.getString("FILE_NAME");
    	param.put("DATA_TYPE", datatype);
    	param.put("OPRNUMB", oprnumb);
    	param.put("FILE_NAME", filename);
    	IData synInfo = this.querySynProductInfo(param);
    	
		inData.put("KIND_ID","BIP2B298_T2001098_0_0");//交易唯一标识
		inData.put("ROUTETYPE", "00");
		inData.put("ROUTEVALUE", "000");
		inData.put("PROV_CODE", this.getProvCode());
		inData.put("PKG_SEQ", this.getProvCode()+SeqMgr.getPkgSeqId());  //交易包流水号
		inData.put("SP_ID", synInfo.getString("FIELD1"));
		inData.put("SP_SERV_ID", synInfo.getString("FIELD2"));
		//产品列表构造
		product.put("SEQ", synInfo.getString("FILE_NAME"));
		product.put("PRODUCT_ID", synInfo.getString("FIELD3"));
		product.put("PRODUCT_NAME", synInfo.getString("FIELD4"));
		product.put("OPR_CODE", "04");  //上线延期申请
		product.put("REASON", "未能及时配置产品，申请延期");    //延期原因
		product.put("SEQ", synInfo.getString("FILE_NAME"));
		productList.add(product);
		inData.put("PRODUCT_LIST", productList);
		IDataset result = IBossCall.callHttpIBOSS("IBOSS", inData);
	    if ((result == null))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        }
        if (result.isEmpty())
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (result.size() < 1)
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (!"0000".equals(result.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RSPDESC", ""));
        }
		if(result!=null && "0".equals(result.getData(0).getString("X_RESULTCODE","")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean CallIBossHttpForReminder(IData date) throws Exception{
		IData inData= new DataMap();
		IData param= new DataMap();
		IData product= new DataMap();
		IDataset productList= new DatasetList();
	  	String datatype = date.getString("DATA_TYPE");
    	String oprnumb = date.getString("OPRNUMB");
    	String filename = date.getString("FILE_NAME");
    	param.put("DATA_TYPE", datatype);
    	param.put("OPRNUMB", oprnumb);
    	param.put("FILE_NAME", filename);
    	IData synInfo = this.querySynProductInfo(param);
    	
		inData.put("KIND_ID","BIP2B298_T2001098_0_0");//交易唯一标识
		inData.put("ROUTETYPE", "00");
		inData.put("ROUTEVALUE", "000");
		inData.put("PROV_CODE", this.getProvCode());
		inData.put("PKG_SEQ", this.getProvCode()+SeqMgr.getPkgSeqId());  //交易包流水号
		inData.put("SP_ID", synInfo.getString("FIELD1"));
		inData.put("SP_SERV_ID", synInfo.getString("FIELD2"));
		//产品列表构造
		product.put("SEQ", synInfo.getString("FILE_NAME"));
		product.put("PRODUCT_ID", synInfo.getString("FIELD3"));
		product.put("PRODUCT_NAME", synInfo.getString("FIELD4"));
		product.put("OPR_CODE", "07");  //催办反馈
		product.put("REASON", "催办的产品已经配置完成");    
		product.put("SEQ", synInfo.getString("FILE_NAME"));
		productList.add(product);
		inData.put("PRODUCT_LIST", productList);
		IDataset result = IBossCall.callHttpIBOSS("IBOSS", inData);
	    if ((result == null))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        }
        if (result.isEmpty())
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (result.size() < 1)
            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
        if (!"0000".equals(result.getData(0).getString("X_RSPCODE", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RSPDESC", ""));
        }
		if(result!=null && "0".equals(result.getData(0).getString("X_RESULTCODE","")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	private IData querySynProductInfo(IData param) throws Exception{
		IData result = new DataMap();
		StringBuilder sql = new StringBuilder();
    	sql.append("SELECT DATA_TYPE,FILE_NAME,OPRNUMB,FIELD1,FIELD2,FIELD3,FIELD4,FIELD5,FIELD6,FIELD7,FIELD8,");
    	sql.append("FIELD9,FIELD10,FIELD11,FIELD12,FIELD13,FIELD14,FIELD15,FIELD16,FIELD17,FIELD18,FIELD19,FIELD20,");
    	sql.append("INSERT_TIME,DEAL_FLAG,DEAL_TIME,RESULT_CODE,RESULT_INFO,PROC_ID,OPER_TYPE,VALID_DATE,EXPIRE_DATE,TIME_STAMP");
        sql.append(" FROM TI_O_DATAIMPORT_DETAIL WHERE DATA_TYPE= '").append(param.getString("DATA_TYPE")).append("'") ; 
        sql.append(" AND OPRNUMB='").append(param.getString("OPRNUMB")).append("'") ; 
        sql.append(" AND FILE_NAME='").append(param.getString("FILE_NAME")).append("'") ; 
    	
    	IDataset results = Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    	
		if(null != results && 0<results.size())
		{
			result =  results.getData(0);
		}
		return result;
	}

	public void updateDealFlag(IData date) throws Exception{
		IData inData=new DataMap();
    	String datatype = date.getString("DATA_TYPE");
    	String oprnumb = date.getString("OPRNUMB");
    	String filename = date.getString("FILE_NAME");
    	inData.put("DATA_TYPE", datatype);
    	inData.put("OPRNUMB", oprnumb);
    	inData.put("FILE_NAME", filename);
    	StringBuilder sql = new StringBuilder();
    	
    	sql.append("UPDATE TI_O_DATAIMPORT_DETAIL T SET T.DEAL_FLAG='1' , T.DEAL_TIME=SYSDATE , T.RESULT_CODE='0' , T.RESULT_INFO='配置成功'");
    	sql.append("WHERE T.DATA_TYPE=:DATA_TYPE AND T.FILE_NAME=:FILE_NAME AND T.OPRNUMB=:OPRNUMB");
    	
		Dao.executeUpdate(sql, inData, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 获取省代码
	 * @return
	 * @throws Exception
	 */
	public String getProvCode() throws Exception
    {
        String provCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode() });

        if (provCode == null || provCode.length() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询省代码无资料！");
        }
        return provCode;
    }
}
