package com.asiainfo.veris.crm.order.soa.frame.bcf.upc;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class CrmSmsReceiptFromUpcSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;

	/**
	 * 0:产品 1，8:服务 2，9:优惠
	 * 
	 * @param input
	 * @throws Exception
	 */
	public IData updateSms(IData input) throws Exception
	{
		IData result = new DataMap();

		String sysTime = SysDateMgr.getSysTime();
		String endTime = SysDateMgr.addSecond(sysTime, -1);

		String offerCode = IDataUtil.chkParam(input, "OFFER_CODE");
		String offerType = IDataUtil.chkParam(input, "OFFER_TYPE");
		String modifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
		
		String workId = input.getString("WORK_ID","");//一级产商品的文件实例号

		IData variableData = new DataMap();
		variableData.put("OFFER_CODE", offerCode);
		variableData.put("OFFER_TYPE", offerType);
		variableData.put("MODIFY_TAG", modifyTag);
		String templateContent = transformAndcheckContent(IDataUtil.chkParam(input, "TEMPLATE_CONTENT"), variableData);

		String templetId = SeqMgr.getSeqTempletId();

		String cancelTag = input.getString("CANCEL_TAG", "0");

		IDataset sms = new DatasetList();

		if (!(offerType.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT) || offerType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) || offerType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT)))
		{
			CSAppException.appError("-1", "只支持产品和优惠！");
		}
		

		if (offerType.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT))
		{
			int enSuc = endSmsInfos(cancelTag, "0", offerCode, modifyTag, templetId, endTime);
			result.put("END_COUNT", enSuc);
		}
		else
		{
			int enSuc = 0;
			enSuc += endSmsInfos(cancelTag, "2", offerCode, modifyTag, templetId, endTime);
			result.put("END_COUNT", enSuc);
		}
		
		int insSuc = insTradeSms(input, sysTime, templetId, templateContent, workId);
		result.put("INS_COUNT", insSuc);
		
		return result;
	}

	private String transformAndcheckContent(String templateContent, IData variableData) throws Exception
	{
		String err = "";
		int idx = templateContent.indexOf("$");
		int endIdx = templateContent.indexOf("$", idx + 1);
		while (idx > -1 && endIdx > -1)
		{
			String miscnName = templateContent.substring(idx + 1, endIdx).toUpperCase();
			if (!"".equals(miscnName))
			{
				IData newMisc = StaticInfoQry.getStaticInfoByTypeIdDataId("UPC_TEMPLATE_TO_CRM", miscnName);
				if (IDataUtil.isEmpty(newMisc))
				{
					err += miscnName + "\n";
				}
				else
				{
					String pDataId = newMisc.getString("PDATA_ID","");
					if(StringUtils.isNotEmpty(pDataId))
					{
						if(IDataUtil.isNotEmpty(variableData))
						{
							IDataset dataset = new DatasetList();
					        Iterator iterator = variableData.keySet().iterator();
					        while (iterator.hasNext())
					        {
					            String objkey = (String)iterator.next();
					            String objValue = variableData.getString(objkey);
					            objkey = ":"+objkey;
					            objValue = "'"+objValue+"'";
					            pDataId = pDataId.replace(objkey, objValue);
					        }
						}
						miscnName = pDataId;
					}
					miscnName = "@{" + miscnName + "}";
				}
			}
			else
			{
				miscnName = "$$";
			}
			templateContent = templateContent.substring(0, idx) + miscnName + templateContent.substring(endIdx + 1);

			idx = templateContent.indexOf("$", endIdx + 1);
			endIdx = templateContent.indexOf("$", idx + 1);
		}

		if (!"".equals(err))
		{
			CSAppException.appError("-1", "请先增加以下模版表达式！【" + err.substring(0, err.length() - 1) + "】");
		}
		return templateContent;
	}

	public int endSmsInfos(String cancelTag, String objTypeCode, String objCode, String modifyTag, String remark, String endTime) throws Exception
	{
		IData param = new DataMap();
		param.put("CANCEL_TAG", cancelTag);
		param.put("OBJ_TYPE_CODE", objTypeCode);
		param.put("OBJ_CODE", objCode);
		param.put("MODIFY_TAG", modifyTag);
		param.put("END_DATE", endTime);

		SQLParser sql = new SQLParser(param);

		sql.addSQL("UPDATE TD_B_TRADE_SMS T");
		sql.addSQL(" set END_DATE=to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),REMARK='一级产商品下发新短信模版，新短信模版【" + remark + "】'");
		sql.addSQL(" WHERE T.EVENT_TYPE = 'SucSms'");
		sql.addSQL(" AND T.CANCEL_TAG = :CANCEL_TAG");
		sql.addSQL(" AND T.OBJ_TYPE_CODE=:OBJ_TYPE_CODE ");
		sql.addSQL(" AND T.OBJ_CODE=:OBJ_CODE");
		sql.addSQL(" AND T.MODIFY_TAG=:MODIFY_TAG");
		sql.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE");
		int result = Dao.executeUpdate(sql);
		
		return result;
	}
	
	private int insTradeSms(IData input, String sysTime, String templetId,String templateContent, String workId) throws Exception
	{
		String cancelTag = input.getString("CANCEL_TAG", "0");
		String offerCode = input.getString("OFFER_CODE");
		String offerType = input.getString("OFFER_TYPE");
		String modifyTag = input.getString("MODIFY_TAG");
		String orgId = input.getString("ORG_ID", "");
		String opId = input.getString("OP_ID", "");
		String newTemplateCode = "CRM_SMS_UPC_" + templetId;

		int result = 0;
		
		String typeId = "CRM_SMS_UPC_TRADETYPE";
		if(offerType.equals(BofConst.ELEMENT_TYPE_CODE_PRODUCT))
		{
			typeId = "CRM_SMS_UPC_TRADETYPE_P";
		}else if(offerType.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
		{
			typeId = "CRM_SMS_UPC_TRADETYPE_D";
		}

		IDataset tradeTypeList = StaticUtil.getStaticList(typeId);
		
		if(IDataUtil.isEmpty(tradeTypeList))
			return 0;
		IData param = new DataMap();
		param.put("BRAND_CODE", "ZZZZ");
		param.put("PRODUCT_ID", "-1");
		param.put("CANCEL_TAG", cancelTag);
		param.put("ITEM_INDEX", "999");
		param.put("ORDER_NO", "999");
		param.put("OBJ_TYPE_CODE", "P".equals(offerType) ? "0" : "2");
		param.put("OBJ_CODE", offerCode);
		param.put("MODIFY_TAG", modifyTag);
		param.put("SEND_DELAY", "0");
		param.put("IN_MODE_CODE", "-1");
		param.put("TEMPLATE_ID", newTemplateCode);
		param.put("SMS_TYPE", "S");
		param.put("EVENT_TYPE", "SucSms");
		param.put("START_DATE", sysTime);
		param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		param.put("EPARCHY_CODE", "ZZZZ");
		param.put("REMARK", "一级产商品文件入库的实例号WORK_ID="+workId);
		param.put("UPDATE_TIME", sysTime);
		param.put("UPDATE_DEPART_ID", orgId);
		param.put("UPDATE_STAFF_ID", opId);
		
		for(int i = 0 ; i < tradeTypeList.size(); i++){
			param.put("TRADE_TYPE_CODE", tradeTypeList.getData(i).getString("DATA_ID",""));
			if (Dao.insert("TD_B_TRADE_SMS", param, Route.CONN_CRM_CEN))
			{
				result++;
			}
			
		}
		
		param = new DataMap();
		param.put("TEMPLATE_ID", newTemplateCode);
		param.put("TEMPLATE_TYPE", "0");
		param.put("TEMPLATE_KIND", "0");
		param.put("TEMPLATE_CONTENT1", templateContent);
		param.put("START_DATE", sysTime);
		param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		param.put("UPDATE_STAFF_ID", opId);
		param.put("UPDATE_DEPART_ID", orgId);
		param.put("UPDATE_TIME", sysTime);
		param.put("REMARK", "一级产商品文件入库的实例号WORK_ID="+workId);
		
		Dao.insert("TD_B_TEMPLATE", param, Route.CONN_CRM_CEN);
		return result;
	}
	
	public IData updateReceipt(IData input) throws Exception
	{
		IData result = new DataMap();
		String offerCode = IDataUtil.chkParam(input, "OFFER_CODE");
		String offerType = IDataUtil.chkParam(input, "OFFER_TYPE");
		String modifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
		String template = IDataUtil.chkParam(input, "TEMPLATE_CONTENT");
		
		String workId = input.getString("WORK_ID","");
		IData variableData = new DataMap();
		variableData.put("OFFER_CODE", offerCode);
		variableData.put("OFFER_TYPE", offerType);
		variableData.put("MODIFY_TAG", modifyTag);
		
		template = transformAndcheckContent(template, variableData);

		String cancelTag = input.getString("CANCEL_TAG", "0");
		String sysTime = SysDateMgr.getSysTime();
		String endTime = SysDateMgr.addSecond(sysTime, -1);

		IData param = new DataMap();
		param.put("OFFER_CODE", offerCode);
		param.put("OFFER_TYPE", offerType);
		param.put("CANCEL_TAG", cancelTag);
		param.put("MODIFY_TAG", modifyTag);
		param.put("START_DATE", sysTime);
		param.put("END_DATE", endTime);

		param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		param.put("UPDATE_DEPART_ID", input.getString("ORG_ID", ""));
		param.put("UPDATE_STAFF_ID", input.getString("OP_ID", ""));

		int charLength = SmsSend.getCharLength(template, 500);// 截取字符位置
		int i = 1;
		while (charLength < template.length() && i <= 5)// 需要截取
		{
			String template1 = template.substring(0, charLength);
			param.put("TEMPLATE_CONTENT" + i++, template1);
			template = template.substring(charLength);
			charLength = SmsSend.getCharLength(template, 500);// 截取字符位置
		}
		if (i > 5)
		{
			CSAppException.appError("-1", "模版内容过长！");
		}
		param.put("TEMPLATE_CONTENT" + i, template);
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
			result.put("END_COUNT", enSuc);
		}
		
		if(StringUtils.isNotEmpty(template))
		{
			comParam.put("PARAM_ATTR", paramAttr);
			comParam.put("SUBSYS_CODE", "CSM");
			comParam.put("PARAM_CODE", offerCode);
			comParam.put("PARAM_NAME", offerCode);
			comParam.put("PARA_CODE1", modifyTag);
			comParam.put("PARA_CODE2", offerType);
			comParam.put("PARA_CODE3", "");
			comParam.put("PARA_CODE4", "UPC");
			comParam.put("PARA_CODE23", param.getString("TEMPLATE_CONTENT1",""));
			comParam.put("PARA_CODE24", param.getString("TEMPLATE_CONTENT2",""));
			comParam.put("PARA_CODE25", param.getString("TEMPLATE_CONTENT3",""));
			comParam.put("PARA_CODE22", param.getString("TEMPLATE_CONTENT4",""));
			comParam.put("PARA_CODE21", param.getString("TEMPLATE_CONTENT5",""));
			comParam.put("START_DATE", sysTime);
			comParam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
			comParam.put("EPARCHY_CODE", "ZZZZ");
			comParam.put("UPDATE_STAFF_ID", input.getString("OP_ID", ""));
			comParam.put("UPDATE_DEPART_ID", input.getString("ORG_ID", ""));
			comParam.put("UPDATE_TIME", SysDateMgr.getSysDate());
			comParam.put("REMARK", "一级产商品文件入库的实例号WORK_ID="+workId);
			
			if (Dao.insert("TD_S_COMMPARA", comParam, Route.CONN_CRM_CEN))
			{
				result.put("INS_COUNT", 1);
			}
		}	
		
		
		return result;
	}

}
