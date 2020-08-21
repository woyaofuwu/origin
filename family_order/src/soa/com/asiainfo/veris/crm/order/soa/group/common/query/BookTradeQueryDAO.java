
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;

public class BookTradeQueryDAO
{
    public static IDataset queryErrorInfoTrade(IData param,String routeId) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT *");
        parser.addSQL("   FROM  TF_B_TRADE");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL(" AND SUBSCRIBE_STATE IN('3','6')");
        parser.addSQL(" ORDER BY ACCEPT_DATE DESC");
        return Dao.qryByParse(parser,Route.getJourDb(routeId));
    }

    public static IDataset queryUserCancelTrade(IData param,String routeId) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE", "SEL_CANCELINFO_BY_SN", param,Route.getJourDb(routeId));
    }

    public static IDataset queryTradeInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_CUSTIDNEXTDEALTAG", param);
    }

    public static IDataset queryTradeUserInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_USER", "SEL_TRADE_USER", param,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 集团专线竣工
     */
    public int updateTradeDiscnt(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_DISCNT", "UPD_STARTDATE_BY_TRADEID", param);
    }

    public int upateTradeSrv(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE_SVC", "UPD_TRADESVC_STARTDATE2", param);
    }

    public int updateTrade(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_TRADE_NEXTDEALTAG_EXECTIME_BY_PK", param);
    }

    /**
     * 账后代付
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryGroupAccountInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_ACCOUNT", "GROUP_ACCOUNT_PAY", param);
    }

    public IDataset getRelationAAByActIdATag(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDATAG", param);
    }

    public IDataset getNoteItemList(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.PRINT_LEVEL='2'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser);
    }

    public IDataset filterNoteItems(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select note.* from td_b_noteitem note where 1 = 1");
        parser.addSQL(" and note.NOTE_ITEM like '%' || :NOTE_ITEM || '%'");
        parser.addSQL(" and note.PRINT_LEVEL='2'"); // 一级综合帐目
        parser.addSQL(" and note.TEMPLET_CODE='50000017'");
        parser.addSQL(" order by note.PRINT_ORDER, note.NOTE_ITEM_CODE");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public IDataset qryMebProductList(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_MEB_PRODUCT_QUERY", null);
    }

    /**
     * 根据手机号码，获取用户受限表信息
     */
    public IDataset getAccountUniteBySN(IData inparam, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_LIMIT_BLACKWHITE", "SEL_BY_SERALNUM", inparam, pagination);
    }

    /**
     * 根据集团名称，获取受限表信息
     */
    public IDataset getAccountUniteByCustName(IData inparam) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("select  SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE from TF_F_LIMIT_BLACKWHITE  where  cust_name=:CUST_NAME ");
        return Dao.qryByParse(parser);
    }

    /**
     * 根据ec接入号，获取用户受限表信息
     */
    public IDataset getAccountUniteByBank(IData inparam) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("select  SERIAL_NUMBER,BIZ_IN_CODE,CUST_NAME,ACCEPT_DATE from TF_F_LIMIT_BLACKWHITE  where  BIZ_IN_CODE=:BANK_ACCT_NO ");
        return Dao.qryByParse(parser);
    }

    /**
     * 打印信息查询
     */
    public IDataset queryPrints(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SERIAL_NUMBER_VPMN_NO_PRINT", param, pagination,Route.getJourDb(Route.CONN_CRM_CG));
    }

    public IDataset getUserInfoBySn(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER", "SEL_BY_SN", param);
    }

    public IDataset qryRelationAAByAcctIdAAndB(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT *");
        parser.addSQL("   FROM  TF_F_RELATION_AA");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND ACCT_ID_A = :ACCT_ID_A");
        parser.addSQL(" AND ACCT_ID_B = :ACCT_ID_B");

        return Dao.qryByParse(parser);
    }

    public IDataset getRelationAAByActIdB(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_AA", "SEL_RELATION_BY_ACCTIDB", param);
    }

    public static IDataset getLastDefaultPayRelationByUserID(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_A_PAYRELATION", "SEL_BY_USER_MAX", inparams);
    }

    public static IDataset getTradeTypeLimit(IData inparams) throws Exception
    {
        return Dao.qryByCode("TD_S_TRADETYPE_LIMIT", "SEL_EXISTS_LIMIT_TRADETYPECODE", inparams);
    }

    public static IDataset getSpecPayByUserIdA(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_SPECIALEPAY", "SEL_BY_USERID_USERIDA", inparams);
    }

    public static IDataset getUserDiscntInfo(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNTCODE", inparams);

    }

    public IDataset getPriceDataByProductId(IData inparams) throws Exception
    {
        String paramCode = inparams.getString("PARAM_CODE");

        if (paramCode == null || StringUtils.isBlank(paramCode))
        {
            return Dao.qryByCode("TD_S_COMMPARA", "SEL_ONLY_BY_ATTR_ORDERED", inparams);
        }
        else
        {
            return Dao.qryByCode("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", inparams);
        }
    }

    public static IDataset qryTradeDiscntInfos(IData inParams) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_DISCNT", "SEL_BY_TRADEID_TAG", inParams,Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset qryTradeResInfosByType(IData inParams) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_RES", "SEL_TRADERES_BY_RESTYPE", inParams,Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getDatalineInfoByUserId(IData user) throws Exception
    {
        SQLParser parser = new SQLParser(user);
        parser.addSQL(" SELECT  * ");
        parser.addSQL(" FROM  TF_F_USER_DATALINE");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND USER_ID = :USER_ID ");
        parser.addSQL(" AND END_DATE > SYSDATE ");


        return Dao.qryByParse(parser);
    }
    
    /**
     * 查询需要变更的专线信息 20140625
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryChangeLineInfosForEsop(IData data) throws Exception {
        
        String userId = data.getString("USER_ID", "");
        String brandWidth = data.getString("BANDWIDTH","");
        String priceLine = data.getString("PRICELINE","");
        String productNo = data.getString("PRODUCTNO","");
        String tradeId = data.getString("TRADEID","");
        String cityA = data.getString("CITYA","");
        String cityZ = data.getString("CITYZ","");
        
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", "N001");
        
        inparam.put("RSRV_STR2", brandWidth);
        inparam.put("RSRV_STR3", priceLine);
        inparam.put("RSRV_STR7", productNo);
        inparam.put("RSRV_STR9", tradeId);
        inparam.put("RSRV_STR15", cityA);
        inparam.put("RSRV_STR16", cityZ);
        
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL(" Select PARTITION_ID,USER_ID,RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,to_char(RSRV_DATE1,'yyyy-mm-dd') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd') RSRV_DATE3,to_char(RSRV_DATE4,'yyyy-mm-dd') RSRV_DATE4,to_char(RSRV_DATE5,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5,to_char(RSRV_DATE6,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE6,to_char(RSRV_DATE7,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE7,to_char(RSRV_DATE8,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE8,to_char(RSRV_DATE9,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE9,to_char(RSRV_DATE10,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID,DEPART_ID,TRADE_ID,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" From TF_F_USER_OTHER ");
        parser.addSQL(" Where 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID ");
        parser.addSQL(" AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND RSRV_VALUE_CODE=:RSRV_VALUE_CODE ");
        parser.addSQL(" AND RSRV_STR2=:RSRV_STR2 ");
        parser.addSQL(" AND RSRV_STR3=:RSRV_STR3 ");
        parser.addSQL(" AND RSRV_STR7=:RSRV_STR7 ");
        parser.addSQL(" AND RSRV_STR9=:RSRV_STR9 ");
        parser.addSQL(" AND RSRV_STR15=:RSRV_STR15 ");
        parser.addSQL(" AND RSRV_STR16=:RSRV_STR16 ");
        parser.addSQL(" AND END_DATE>sysdate ");
        IDataset datasetOther = Dao.qryByParse(parser);
    
        return datasetOther;
    }
    
    /**
     * 查询该客户下未完工的订单
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryOrderInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT DISTINCT(T.ORDER_ID),T.PRODUCT_ID,T.SERIAL_NUMBER,T.ACCEPT_DATE ");
        parser.addSQL(" FROM TF_B_TRADE T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.CUST_ID = TO_NUMBER(:CUST_ID) ");
        parser.addSQL(" AND T.PRODUCT_ID IN ('7010','7011','7012','7013','7014','7015') ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }
    /**
     * 根据ORDER_ID查询所有TRADE
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryMainTradeByOrderId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  * ");
        parser.addSQL(" FROM TF_B_TRADE T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        parser.addSQL(" AND T.PRODUCT_ID IN ('7010','7011','7012','7013','7014','7015') ");
        parser.addSQL(" AND T.BRAND_CODE = TO_CHAR(:BRAND_CODE) ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 闭环时手动将订单完工
     * @param param
     * @return
     * @throws Exception
     */
    public int updateOrderState(IData param) throws Exception
    {
        StringBuilder sqlTrade = new StringBuilder();
        sqlTrade.append("UPDATE TF_B_TRADE T SET T.SUBSCRIBE_STATE = 'P' WHERE T.ORDER_ID = :ORDER_ID ");
        
        StringBuilder sqlOrder = new StringBuilder();
        sqlOrder.append("UPDATE TF_B_ORDER T SET T.ORDER_STATE = '2' WHERE T.ORDER_ID = :ORDER_ID ");
        
        int i = Dao.executeUpdate(sqlTrade, param,Route.getJourDb(BizRoute.getRouteId()));
        int b = Dao.executeUpdate(sqlOrder, param,Route.getJourDb(BizRoute.getRouteId()));
        
        if(i > 1 && b > 0){
            return 1;
        }else{
            return 0;
        }
    }
    
    /**
     * 撤单查询该订单信息
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryOrderInfoByOrderId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_ORDER T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");

        return Dao.qryByParse(parser);
    }
    /**
     * 撤单时询该订单的所有TRADE
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeInfoByOrderId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_TRADE T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.ORDER_ID = TO_NUMBER(:ORDER_ID) ");
        parser.addSQL(" AND T.PRODUCT_ID IN ('7010','7011','7012','7013','7014','7015') ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");

        return Dao.qryByParse(parser);
    }
    /**
     * 撤单时删除订单数据
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean  deleteOrderInfoByOrderId(IData param) throws Exception
    {
        return Dao.delete("TF_B_ORDER", param,new String[]{"ORDER_ID"}, Route.CONN_CRM_CG);
    }
    /**
     * 撤单时删除所有TRADE数据
     * @param param
     * @return
     * @throws Exception
     */
    public static boolean deleteTradeInfoByOrderId(IData param) throws Exception
    {
        return Dao.delete("TF_B_TRADE", param,new String[]{"ORDER_ID"},Route.CONN_CRM_CG);
    }
    /**
     * 撤单时将订单移历史表
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] insertOrderHInfo(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_ORDER", param, Route.CONN_CRM_CG);
    }
    /**
     * 撤单时将所有TRADE移历史表
     * @param param
     * @return
     * @throws Exception
     */
    public static int[] insertTradeHInfo(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_TRADE", param, Route.CONN_CRM_CG);
    }
    
    
    public static IDataset checkGroupDatalineUserInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT T.USER_ID,P.PRODUCT_ID,C.CUST_ID ");
        parser.addSQL(" FROM TF_F_USER T, TF_F_USER_PRODUCT P,TF_F_CUST_GROUP C "); 
        parser.addSQL(" WHERE T.USER_ID = P.USER_ID ");
        parser.addSQL(" AND T.CUST_ID = C.CUST_ID ");
        parser.addSQL(" AND P.PRODUCT_ID IN (7010,7011, 7012, 7013, 7014, 7015) ");
        parser.addSQL(" AND T.REMOVE_TAG = '0' ");
        parser.addSQL(" AND P.PRODUCT_ID = :PRODUCT_ID ");
        parser.addSQL(" AND C.GROUP_ID = :GROUP_ID ");

        return Dao.qryByParse(parser);
    } 
    
    public static IDataset getMaxLineNumberByUserId(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT nvl(MAX(TO_NUMBER(T.RSRV_VALUE)),0) AS LINE_NUMBER_CODE ");
        parser.addSQL(" FROM TF_F_USER_OTHER T "); 
        parser.addSQL(" WHERE T.RSRV_VALUE_CODE = 'N001' ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");

        return Dao.qryByParse(parser);
    } 
    /**
     * 修改工单的执行时间为2050
     * @param param
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-15
     */
    public void updateMainTradeExecTimeTo2050(String tradeId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", tradeId);
        StringBuilder sqlTrade = new StringBuilder();
        sqlTrade.append("UPDATE TF_B_TRADE T SET T.EXEC_TIME = TO_DATE('2050-12-31', 'YYYY-MM-DD'),T.UPDATE_TIME=SYSDATE WHERE T.TRADE_ID = :TRADE_ID ");
        
        Dao.executeUpdate(sqlTrade, param, Route.getJourDb(BizRoute.getRouteId()));
    }
    /**
     * 修改工单的执行时间为当前时间
     * @param tradeId
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-15
     */
    public void updateOrderTradeExecTimeToNow(String tradeId) throws Exception
    {
    	IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0");
    	if(IDataUtil.isEmpty(tradeInfo)){
    		tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", "0898");
    		if(IDataUtil.isEmpty(tradeInfo)){
        		CSAppException.apperr(GrpException.CRM_GRP_713, "根据TRADE_ID["+tradeId+"]查询主台账无数据!");
        	}
    	}
    	
    	IData param = new DataMap();
    	String orderId = tradeInfo.getString("ORDER_ID");
    	param.put("ORDER_ID", orderId);
    	StringBuilder sqlTrade = new StringBuilder();
        sqlTrade.append("UPDATE TF_B_TRADE T SET T.EXEC_TIME = SYSDATE,T.UPDATE_TIME=SYSDATE WHERE T.ORDER_ID = :ORDER_ID ");
        
        StringBuilder sqlOrder = new StringBuilder();
        sqlOrder.append("UPDATE TF_B_ORDER T SET T.EXEC_TIME = SYSDATE,T.UPDATE_TIME=SYSDATE WHERE T.ORDER_ID = :ORDER_ID ");
        
        int i = Dao.executeUpdate(sqlTrade, param, Route.getJourDb(BizRoute.getRouteId()));
        if(i<=0){
        	CSAppException.apperr(GrpException.CRM_GRP_713, "根据ORDER_ID["+orderId+"]更新主台账[TF_B_TRADE]无数据!");
        }
        int j = Dao.executeUpdate(sqlOrder, param, Route.getJourDb(BizRoute.getRouteId()));
        
    }
	public static IDataset getDatalineInfoByProductNo(IData data) throws Exception
    {
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT  * ");
        parser.addSQL(" FROM  TF_F_USER_DATALINE");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND PRODUCT_NO = :PRODUCT_NO ");
        parser.addSQL(" AND END_DATE > SYSDATE ");


        return Dao.qryByParse(parser);
    }
    /**
     * 查询相关专线信息 20140625
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset queryLineInfosForProductNo(IData data) throws Exception {
        
        String userId = data.getString("USER_ID", "");
        String productNo = data.getString("PRODUCTNO","");
        
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", "N001");
        if(!productNo.equals("")){
        	inparam.put("RSRV_STR7", productNo);
        }
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL(" Select PARTITION_ID,USER_ID,RSRV_VALUE_CODE,RSRV_VALUE,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_NUM6,RSRV_NUM7,RSRV_NUM8,RSRV_NUM9,RSRV_NUM10,RSRV_NUM11,RSRV_NUM12,RSRV_NUM13,RSRV_NUM14,RSRV_NUM15,RSRV_NUM16,RSRV_NUM17,RSRV_NUM18,RSRV_NUM19,RSRV_NUM20,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,RSRV_STR11,RSRV_STR12,RSRV_STR13,RSRV_STR14,RSRV_STR15,RSRV_STR16,RSRV_STR17,RSRV_STR18,RSRV_STR19,RSRV_STR20,RSRV_STR21,RSRV_STR22,RSRV_STR23,RSRV_STR24,RSRV_STR25,RSRV_STR26,RSRV_STR27,RSRV_STR28,RSRV_STR29,RSRV_STR30,to_char(RSRV_DATE1,'yyyy-mm-dd') RSRV_DATE1,to_char(RSRV_DATE2,'yyyy-mm-dd') RSRV_DATE2,to_char(RSRV_DATE3,'yyyy-mm-dd') RSRV_DATE3,to_char(RSRV_DATE4,'yyyy-mm-dd') RSRV_DATE4,to_char(RSRV_DATE5,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE5,to_char(RSRV_DATE6,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE6,to_char(RSRV_DATE7,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE7,to_char(RSRV_DATE8,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE8,to_char(RSRV_DATE9,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE9,to_char(RSRV_DATE10,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE10,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3,RSRV_TAG4,RSRV_TAG5,RSRV_TAG6,RSRV_TAG7,RSRV_TAG8,RSRV_TAG9,RSRV_TAG10,PROCESS_TAG,STAFF_ID,DEPART_ID,TRADE_ID,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,INST_ID ");
        parser.addSQL(" From TF_F_USER_OTHER ");
        parser.addSQL(" Where 1=1 ");
        parser.addSQL(" AND USER_ID=:USER_ID ");
        parser.addSQL(" AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000) ");
        parser.addSQL(" AND RSRV_VALUE_CODE=:RSRV_VALUE_CODE ");
        parser.addSQL(" AND (RSRV_STR7=:RSRV_STR7 or :RSRV_STR7 is null) ");
        parser.addSQL(" AND END_DATE>sysdate ");
        IDataset datasetOther = Dao.qryByParse(parser);
    
        return datasetOther;
    }
}
