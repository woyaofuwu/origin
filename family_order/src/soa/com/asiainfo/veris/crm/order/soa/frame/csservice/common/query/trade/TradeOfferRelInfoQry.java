
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.bizcommon.util.StrUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;


public class TradeOfferRelInfoQry
{
    /**
     * 更新商品关系开始时间
     * 
     * @author yuyj3
     * @param tradeId
     * @param relOfferType
     * @param startDate
     * @throws Exception
     */
    public static void updateStartDate(String tradeId, String relOfferType, String startDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("REL_OFFER_TYPE", relOfferType);
        param.put("START_DATE", startDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OFFER_REL", "UPD_TRADE_OFFER_REL_STARTDATE", param, Route.getJourDb());
    }
    
    
    /**
     * 更新商品关系开始时间、结束时间
     * @param tradeId
     * @param relOfferInsId
     * @param startDate
     * @param endDate
     * @throws Exception
     * @author yuyj3
     */
    public static void updateStartAndEndDate(String tradeId, String relOfferInsId, String startDate, String endDate) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("REL_OFFER_INS_ID", relOfferInsId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OFFER_REL", "UPD_TRADE_OFFER_REL_STARTENDDATE", param, Route.getJourDb());
    }
    
    public static IDataset getOfferRelByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_OFFER_REL", "SEL_BY_TRADEID", param, Route.getJourDb());
    }
    public static IDataset getOfferRelByTradeIdAndOfferType(String tradeId,String offerType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("OFFER_TYPE", offerType);
        return Dao.qryByCode("TF_B_TRADE_OFFER_REL", "SEL_BY_TRADEID_AND_OFFERTYPE", param, Route.getJourDb());
    }
    
    public static IDataset getOfferRelBakByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADE_OFFER_REL_BAK", "SEL_BY_TRADEID", param);
    }
    
    public static IDataset queryTradeOfferRelsByUserId(String userId, String relOfferInsId) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.OFFER_CODE,A.OFFER_TYPE,A.OFFER_INS_ID,A.USER_ID,A.GROUP_ID,A.REL_OFFER_CODE,A.REL_OFFER_TYPE,A.REL_OFFER_INS_ID,A.REL_USER_ID,A.REL_TYPE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.INST_ID");
    	sql.append(" FROM TF_B_TRADE_OFFER_REL A, TF_B_TRADE B");
    	sql.append(" WHERE A.REL_USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND A.REL_OFFER_INS_ID = TO_NUMBER(:REL_OFFER_INS_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	sql.append(" AND B.SUBSCRIBE_TYPE != '600' ");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb());
        return ids; 
    }
    
    public static IDataset queryTradeOfferRelsByUserId(String userId, String relOfferInsId,String tradeId) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	param.put("TRADE_ID", tradeId);
    	if(StringUtils.isNotBlank(tradeId))
    	{
    		param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
    	}
    	
    	StringBuilder sql = new StringBuilder();
    	sql.append(" SELECT A.TRADE_ID,A.ACCEPT_MONTH,A.OFFER_CODE,A.OFFER_TYPE,A.OFFER_INS_ID,A.USER_ID,A.GROUP_ID,A.REL_OFFER_CODE,A.REL_OFFER_TYPE,A.REL_OFFER_INS_ID,A.REL_USER_ID,A.REL_TYPE,TO_CHAR(A.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(A.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,A.MODIFY_TAG,TO_CHAR(A.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,A.UPDATE_STAFF_ID,A.UPDATE_DEPART_ID,A.REMARK,A.INST_ID");
    	sql.append(" FROM TF_B_TRADE_OFFER_REL A, TF_B_TRADE B");
    	sql.append(" WHERE A.REL_USER_ID = TO_NUMBER(:USER_ID)");
    	sql.append(" AND A.REL_OFFER_INS_ID = TO_NUMBER(:REL_OFFER_INS_ID)");
    	sql.append(" AND B.TRADE_ID = A.TRADE_ID");
    	sql.append(" AND B.TRADE_ID = TO_NUMBER(:TRADE_ID)");
    	sql.append(" AND B.ACCEPT_MONTH = A.ACCEPT_MONTH");
    	if(StringUtils.isNotBlank(tradeId))
    	{
    		sql.append(" AND B.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)");
    	}
    	sql.append(" AND B.SUBSCRIBE_TYPE != '600' ");
    	IDataset ids = Dao.qryBySql(sql, param, Route.getJourDb());
        return ids; 
    }
    public static IDataset  getOfferInfoByUserId(String productUserId,String elementId) throws Exception{
    	IData param = new DataMap();
    	param.put("USER_ID", productUserId);
    	param.put("REL_OFFER_CODE", elementId);
    	return Dao.qryByCodeParser("TF_B_TRADE_OFFER_REL", "SEL_BY_USER_ID", param, Route.getJourDb());
    }
   
}
