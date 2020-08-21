package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryListInfo 
{
	public static IDataset queryListInfoForRelation(IData param) throws Exception 
	{
		
		IDataset retnList = Dao.qryByCode("TD_B_CTRM_RELATION", "SEL_BY_CTRM_PRODUCT_ID", param,Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	//REQ201807300022关于移动商城4G飞享合约资费套餐同步扩容的需求 wuhao5 190329
	public static IDataset queryProuductIdByType(String type) throws Exception 
	{
		IData param = new DataMap();
		
		param.put("CTRM_PRODUCT_TYPE", type);
		
		IDataset retnList = Dao.qryByCode("TD_B_CTRM_RELATION", "SEL_BY_CTRM_PRODUCT_TYPE", param,Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	
	public static IDataset queryListInfoForTlist(IData param) throws Exception 
	{
		
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_SUBORDER_BY_TID", param,	Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	
	public static IDataset queryListInfoForTidTlist(IData param) throws Exception 
	{
		
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PROORDER_BY_TID_OID", param, Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	
	public static IDataset queryListInfoForOrderTlist(IData param) throws Exception 
	{
		
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRM_ORDER_BY_TID_OID", param, Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	
	public static IDataset queryListInfoForProorderTlist(IData param) throws Exception 
	{
		
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PROORDER_BY_CTRMTYPE", param, Route.CONN_CRM_CEN);
		
		return (retnList == null) ? new DatasetList():retnList;
	}
	
	public static IDataset queryCtrmtListByID(IData param) throws Exception 
	{
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRMTLIST_BY_TID", param, Route.CONN_CRM_CEN);
		return (retnList == null) ? new DatasetList() : retnList;
	}
	
	public static IDataset queryCtrmtOrderByID(IData param) throws Exception 
	{
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRMORDER_BY_TID", param, Route.CONN_CRM_CEN);
		return (retnList == null) ? new DatasetList() : retnList;
	}
	
	public static IDataset queryCtrmtProductByID(IData param) throws Exception 
	{
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRMPRODUCT_BY_TID", param, Route.CONN_CRM_CEN);
		return (retnList == null) ? new DatasetList() : retnList;
	}
	
	public static IDataset queryCtrmOrderAttrByID(IData param) throws Exception 
	{
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_CTRMORDERATTR_BY_OID", param, Route.CONN_CRM_CEN);
		return (retnList == null) ? new DatasetList() : retnList;
	}
	
	/**
	 * 获取表中指定字段的值
	 * @param updateOrder
	 * @throws Exception
	 */
	public static String queryColumnValueById(IData param,String colString) throws Exception
	{
		IDataset restList = Dao.qryByCode("TF_B_TRADE_SALE_ACTIVE", "SEL_BY_TRADE_ID", param, Route.getJourDb(BizRoute.getRouteId()));
		//modify by duhj 订单库
		if(restList != null && restList.size() > 0)
		{
			return restList.getData(0).getString(colString);
		}
		return "";
	}
	
	
	/**
	 * 获取表中指定字段的值
	 * @param updateOrder
	 * @throws Exception
	 */
	public static String queryColumnValueByType(IData param,String colString) throws Exception
	{
		IDataset restList = Dao.qryByCode("TF_B_CTRM_TLIST", "SEL_PROORDER_BY_CTRMTYPE", param, Route.CONN_CRM_CEN);
		if(restList != null && restList.size() > 0)
		{
			return restList.getData(0).getString(colString);
		}
		return "";
	}
	
	public static String getSeqId() throws Exception
	{
		IData inData = new DataMap();
		SQLParser sql= new SQLParser(inData);
		sql.addSQL("select SEQ_CTRM_PROID.nextval NEXTVAL from dual");
		IDataset seqList = Dao.qryByParse(sql,Route.CONN_CRM_CEN);
		String sequence = (seqList != null && seqList.size() > 0)? seqList.getData(0).getString("NEXTVAL"):"";
		
        String nowDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
        
        String formatSeq = fillStr(sequence, "0", 8, true);
        StringBuilder seqLogId = new StringBuilder();
        seqLogId.append(nowDate);
        seqLogId.append(formatSeq);
        return seqLogId.toString();
	}
	
	/**
	 * 填充字符串 例：数据库生成了5位的数字sequence，但构成流水号需要8位，那么可通过此方法，在左边补零
	 * 
	 * @param srcStr 原始字符串
	 * @param fillStr  要填充到原始字符串中的字符串
	 * @param totalLen 填充后的总长度
	 * @param leftFlag   左填充标志 为true在srcStr左边填充fillStr 为false在srcStr右边填充fillStr
	 * @return
	 */
	public  static String fillStr(String srcStr, String fillStr, int totalLen,boolean leftFlag)
	{
		if (srcStr == null)
		{
			return null;
		}
		if (srcStr.length() > totalLen || fillStr == null || fillStr.length() == 0)
		{
			return srcStr;
		}
		if (((totalLen - srcStr.length()) % fillStr.length()) != 0)
		{
			return srcStr;
		}
		String result = srcStr;
		
		int i = totalLen - srcStr.length();
		while (i > 0)
		{
			result = leftFlag ? (fillStr + result) : (result + fillStr);
			i = i - fillStr.length();
		}
		return result;
	}
	
    public static IDataset getCustInfoByPsptCustType(String cust_type, String pspt_type_code, String pspt_id) throws Exception
    {   
    	IDataset list=null;
        IData params = new DataMap();
        params.put("CUST_TYPE", cust_type);
        params.put("PSPT_ID", pspt_id);
       
        String[] ch = pspt_type_code.split(",");
        for(int i =0 ;i<ch.length;i++)
        {
          params.put("PSPT_TYPE_CODE", ch[i]);
          list = Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_ALLPSPT", params);
          if(list!=null && list.size()>0)
          {
        	  return list;
          }
        }
        return list;
    }
    
    public static IDataset getUserInfoByCustId(String cust_id, String user_diff_code) throws Exception
    {
        IData param = new DataMap();
        param.put("CUST_ID", cust_id);
        param.put("USER_DIFF_CODE", user_diff_code);

        return Dao.qryByCode("TF_F_USER", "SEL_BY_NCUST_ID", param);
    }

	public static IDataset queryRelationInfoByElementIdType(IData param) throws Exception
	{
		IDataset retnList = Dao.qryByCode("TD_B_CTRM_RELATION", "SEL_BY_ELEMENT_ID_TYPE", param,Route.CONN_CRM_CEN);

		return (retnList == null) ? new DatasetList():retnList;
	}

	public static IDataset queryOrderProductInfoToDeal(IData param) throws Exception
	{
		IDataset retnList = Dao.qryByCode("TF_B_CTRM_ORDER_PRODUCT", "SEL_BY_TID_OID_TO_DEAL", param, Route.CONN_CRM_CEN);

		return (retnList == null) ? new DatasetList() : retnList;
	}

	public static IDataset qryCtrmOrderProductInfoByTradeId(String tradeId) throws Exception {
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		return Dao.qryByCodeParser("TF_B_CTRM_ORDER_PRODUCT", "SEL_BY_TRADE_ID", param, Route.CONN_CRM_CEN);
	}

	public static IDataset getPhoneUserInfoByCustId(String cust_id) throws Exception{
		IData param = new DataMap();
		param.put("CUST_ID", cust_id);

		return Dao.qryByCode("TF_F_USER", "SEL_PHONE_BY_CUST_ID", param);
	}
}
