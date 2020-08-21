package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.secconfirmlog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class SecConfirmLogQry extends CSBizBean {
	/**
	 * 记录二次确认日志信息
	 */
	public static boolean recordSecConfirmLog(IData input) throws Exception{
		IData insertdata = new DataMap();
		insertdata.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		insertdata.put("USER_ID", input.getString("USER_ID"));
		insertdata.put("ORDER_ID",input.getString("ORDER_ID"));
		insertdata.put("TRADE_ID1",input.getString("TRADE_ID1"));
		insertdata.put("TRADE_ID2",input.getString("TRADE_ID2"));
		insertdata.put("INST_ID",input.getString("INST_ID"));
		insertdata.put("INST_TYPE",input.getString("INST_TYPE"));
		insertdata.put("COMMODITY_CODE",input.getString("COMMODITY_CODE"));
		insertdata.put("SERV_TYPE",input.getString("SERV_TYPE"));
		insertdata.put("SP_CODE",input.getString("SP_CODE"));
		insertdata.put("OPERATOR_CODE",input.getString("OPERATOR_CODE"));
		insertdata.put("MEMBER_TYPE",input.getString("MEMBER_TYPE"));
		insertdata.put("CHANNEL_SOURCE",input.getString("CHANNEL_SOURCE"));
		insertdata.put("RE_WAY",input.getString("RE_WAY"));
		insertdata.put("BOOK_TIME",input.getString("BOOK_TIME"));
		insertdata.put("CONFIRM_TIME",input.getString("CONFIRM_TIME"));
		insertdata.put("CREATE_TIME",input.getString("CREATE_TIME"));
		insertdata.put("CONFIRM_LOG",input.getString("CONFIRM_LOG"));
		insertdata.put("ELEMENT_ID",input.getString("ELEMENT_ID"));
		insertdata.put("SOURCE_TAG",input.getString("SOURCE_TAG","0"));
		insertdata.put("UPLOAD_TAG",input.getString("UPLOAD_TAG","0"));
		
		return Dao.insert("TL_B_ACCEPTSECCONFIRM_LOG", insertdata, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 业务取消将业务受理日志记录搬到业务取消日志记录表
	 */
	public static int recordCancelSecConfirmLog(IData input) throws Exception{
		return Dao.executeUpdateByCodeCode("TL_B_CANCELSECCONFIRM_LOG", "INS_CANCEL_CONFIRMLOG", input, Route.CONN_CRM_CEN);
	}
	
	/**
	 * 业务受理表日志删除
	 */
	public static boolean deleteSecConfirmLog(IData input) throws Exception{
		IData deldata = new DataMap();
		deldata.put("INST_ID", input.getString("INST_ID"));
		return Dao.delete("TL_B_ACCEPTSECCONFIRM_LOG", deldata,new String[]{ "INST_ID" }, Route.CONN_CRM_CEN);
	}
	
	
	public static IDataset querySecConfirmLog(IData input) throws Exception{








//		return Dao.qryByCode("TL_B_ACCEPTSECCONFIRM_LOG", "SEL_SECCONFIRMLOG", input, Route.CONN_CRM_CEN);


//		  IData param = new DataMap();
//		 param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
//		 param.put("SERV_TYPE", input.getString("SERV_TYPE"));
//		 param.put("BOOK_TIME", input.getString("STRAT_BOOK_TIME"));
//		 param.put("CONFIRM_TIME",  input.getString("END_BOOK_TIME"));

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT T.INST_TYPE,T.SERIAL_NUMBER,T.ORDER_ID,T.TRADE_ID1,T.TRADE_ID2,T.COMMODITY_CODE,T.SERV_TYPE,T.SP_CODE,T.OPERATOR_CODE,T.MEMBER_TYPE,T.CHANNEL_SOURCE,T.RE_WAY,T.BOOK_TIME,T.CONFIRM_TIME,T.CREATE_TIME,T.CONFIRM_LOG,T.ELEMENT_ID,T.SOURCE_TAG,T.UPLOAD_TAG,T.USER_ID FROM ");
		sql.append(	"(SELECT T1.INST_TYPE,T1.SERIAL_NUMBER,T1.ORDER_ID,T1.TRADE_ID1,T1.TRADE_ID2,T1.COMMODITY_CODE,T1.SERV_TYPE,T1.SP_CODE,T1.OPERATOR_CODE,T1.MEMBER_TYPE,   ");
		sql.append(	"T1.CHANNEL_SOURCE,T1.RE_WAY,T1.BOOK_TIME,T1.CONFIRM_TIME,T1.CREATE_TIME,T1.CONFIRM_LOG,T1.ELEMENT_ID,T1.SOURCE_TAG,T1.UPLOAD_TAG,T1.RSRV_STR1,T1.RSRV_STR2,T1.RSRV_STR3,T1.RSRV_STR4,T1.RSRV_STR4,T1.RSRV_STR5,T1.USER_ID " );
		sql.append(	"FROM TL_B_ACCEPTSECCONFIRM_LOG T1  " );
		sql.append(	"UNION  " );
		sql.append(	"SELECT T2.INST_TYPE,T2.SERIAL_NUMBER,T2.ORDER_ID,T2.TRADE_ID1,T2.TRADE_ID2,T2.COMMODITY_CODE,T2.SERV_TYPE,T2.SP_CODE,T2.OPERATOR_CODE,T2.MEMBER_TYPE,  " );
		sql.append("T2.CHANNEL_SOURCE,T2.RE_WAY,T2.BOOK_TIME,T2.CONFIRM_TIME,T2.CREATE_TIME,T2.CONFIRM_LOG,T2.ELEMENT_ID,T2.SOURCE_TAG,T2.UPLOAD_TAG,T2.RSRV_STR1,T2.RSRV_STR2,T2.RSRV_STR3,T2.RSRV_STR4,T2.RSRV_STR4,T2.RSRV_STR5 ,T2.USER_ID " );
		sql.append(	"FROM TL_B_CANCELSECCONFIRM_LOG T2) T  " );
		sql.append("WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER" );
//		if(StringUtils.isNotEmpty(input.getString("SERV_TYPE"))){
//			sql.append(" and t.SERV_TYPE =:SERV_TYPE	");
//		}
		if(StringUtils.isNotEmpty(input.getString("SERV_TYPE"))){//业务类别
			if (input.getString("SERV_TYPE").equals("01")){//基础业务
				sql.append(" and t.INST_TYPE in ('S','D','P','A')");

			}else if (input.getString("SERV_TYPE").equals("02")){//增值业务
				sql.append(" and t.INST_TYPE in ('Z')");
			}
		}
		if(StringUtils.isNotEmpty(input.getString("BOOK_TIME"))){
			sql.append(" and t.BOOK_TIME >=to_date(:BOOK_TIME,'yyyy-mm-dd') ");
		}
		if(StringUtils.isNotEmpty(input.getString("CONFIRM_TIME"))){
			sql.append(" and t.CONFIRM_TIME <=to_date(:CONFIRM_TIME,'yyyy-mm-dd')");
		}

		return Dao.qryBySql(sql, input, Route.CONN_CRM_CEN);
	}
	
	
	public static void cleanSecConfirmLog(IData input) throws Exception{
	   for (String routeId : Route.getAllJourDb())
       {
		   Dao.executeUpdateByCodeCode("TL_B_CANCELSECCONFIRM_LOG", "DEL_CANCEL_CONFIRMLOG", input, Route.getJourDb(routeId));
       }
	}

	//查询业务订购二次确认局数据  入参：COMMODITY_CODE
	public static IDataset queryOrderReconfirmByCommodityCodes(String commodityCode) throws Exception{
		return UpcCall.queryOrderReconfirm(commodityCode);
	}

	//查询业务订购二次确认特例局数据  入参：COMMODITY_CODE（商品编码）+ SERV_TYPE（业务类型）+ SP_CODE（企业代码）+OPERATOR_CODE（业务代码）+MEMBER_TYPE（会员属性）+ CHANNEL_SOURCE（操作渠道来源代码）
	public static IDataset querySepcReconfirmCond(IData input) throws Exception{
		IData param = new DataMap();
		String commodityCode = input.getString("COMMODITY_CODE");
		String spCode = input.getString("SP_CODE");
		String bizCode = input.getString("OPERATOR_CODE");
		if(StringUtils.isNotEmpty(commodityCode)){
			param.put("COMMODITY_CODE", commodityCode);
		}else if(StringUtils.isNotEmpty(spCode) && StringUtils.isNotEmpty(bizCode)){
			param.put("SP_CODE", input.getString("SP_CODE"));
			param.put("OPERATOR_CODE", input.getString("OPERATOR_CODE"));
		}
		param.put("CHANNEL_SOURCE", input.getString("CHANNEL_SOURCE"));
		return UpcCall.querySepcReconfirmCond(param);
	}


	//查询业务订购二次确认信息局数据  入参：CHANNEL_SOURCE
	public static IDataset queryChannelReconfirmByCond(String channelSource) throws Exception{
		IData param = new DataMap();
		param.put("CHANNEL_SOURCE", channelSource);
		return UpcCall.queryChannelReconfirm(channelSource);
	}

   public static String getCrmProductsInfo(String elementID,String elementTypeCode,String eparchyCode) throws Exception {
       IData qryParam = new DataMap();
	   qryParam.put("ELEMENT_ID", elementID);
	   qryParam.put("ELEMENT_TYPE_CODE",
			   elementTypeCode);
	   qryParam.put("EPARCHY_CODE", eparchyCode);
	   IDataset result = Dao.qryByCode("TD_B_CTRM_RELATION","SEL_CRMPRODUCT",qryParam, Route.CONN_CRM_CEN);
       IData crmProduct = new DataMap();
       if (IDataUtil.isNotEmpty(result)) {
           crmProduct = result.getData(0);
       }
       return IDataUtil.isNotEmpty(crmProduct) ? crmProduct.getString("CTRM_PRODUCT_ID") : "";
   }
   
   public static String getChannelSource(String inmodeCode)throws Exception {
       String channelSource = "";
       IDataset channelSourceinfo = CommparaInfoQry.getCommparaInfoByCode("CSM","5471",inmodeCode,"TWO_SMS_CHANNELSOURCE","ZZZZ");
       if (IDataUtil.isNotEmpty(channelSourceinfo))
       {
           channelSource = channelSourceinfo.getData(0).getString("PARA_CODE2");
       }
       return channelSource;
   }
	
}
