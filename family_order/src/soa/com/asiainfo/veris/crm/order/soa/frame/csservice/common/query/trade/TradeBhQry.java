
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class TradeBhQry
{
    public static boolean getMaxAdvancePayTradeInfoByTid(String tradeId, String advancePay) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ADVANCE_PAY", advancePay);
        IDataset tradeDataset = Dao.qryByCode("TF_BH_TRADE", "SEL_MAX_ADVPAY_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (IDataUtil.isNotEmpty(tradeDataset))
        {
            return true;
        }
        return false;
    }
    /**
     * 根据ORDER_id查询
     * 
     * @author zhangcheng6
     * @param data
     * @param pagination
     * @return dataset
     * @throws Exception
     */
//    public static int insertBhOrderByOrderId(IData params) throws Exception
//    {
//        return  Dao.executeUpdateByCodeCode("TF_B_ORDER", "INS_BHORDER_BY_TRADEID", params, Route.getJourDb(Route.CONN_CRM_CG));
//    }
//    
    /**
     * 根据trade_id查询
     * 
     * @author zhangcheng6
     * @param //data
     * @param //pagination
     * @return dataset
     * @throws Exception
     */
    public static int insertBhTradeByTradeId(IData params) throws Exception
    {
    	params.put("CANCEL_TAG", "0");
        return  Dao.executeUpdateByCodeCode("TF_BH_TRADE", "INSERT_FROM_TRADE", params, Route.getJourDb(Route.CONN_CRM_CG));
    }
    public static String getMaxOpenTradeInfoBySn(String serialNumber, String maxDay) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("MAX_DAY", maxDay);
        IDataset tradeDataset = Dao.qryByCode("TF_BH_TRADE", "SEL_MAX_OPEN_TRADE_ID", param, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        if (IDataUtil.isNotEmpty(tradeDataset))
        {
            return tradeDataset.getData(0).getString("TRADE_ID");
        }
        return null;
    }

    public static IDataset getTradeBhInfoByUserIdTradeTypeCode(String userId, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_LAST_TRADE", param, Route.getJourDb());
    }

    public static IDataset getTradeBhInfos(String serial_number, String userId, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERIAL_NUMBER", serial_number);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_TRADETYPEOCDE", param, Route.getJourDb());
    }

    public static IDataset getTradeBhInfosByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_LAST_TRADEID_BY_SN_NPTRADETYPECODE", param);
    }

    public static IDataset getTradeInfosByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_RESTORE", param);
    }

    /**
     * 根据手机号、用户ID和业务类型查询
     * 
     * @param sn
     * @param userId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-15 09:59:41
     */
    public static IDataset qryBhTradeBySn(String sn, String userId, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("SERIAL_NUMBER", sn);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("USER_ID", userId);
        param.put("NUM", "2");// 查询2天内的

        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_CODE_DAY", param);
    }

    public static IDataset qryTradeInfosByPKAndDate(String tradeId, String startDate, String endDate, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("START_DATE", startDate);
        param.put("END_DATE", endDate);

        return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_BY_PK_DATE", param, pagination);
    }
    public static IDataset queryTradeInfoByPK(String tradeId, String cancelTag) throws Exception
	{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);
		param.put("CANCEL_TAG", cancelTag);
		return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_PK", param, Route.getJourDb());
	}

    public static IDataset queryBossInfoByCodeCode(IData param) throws Exception
    {
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_BOSSORDER", param);
    }

    /**
     * 查询转赠积分
     * 
     * @author chenghr
     * @param data
     * @param pagination
     * @return dataset
     * @throws Exception
     */
    public static IDataset queryPresentScoreByCity(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_QUERYSCOREDONATE_2", data, pagination);

        return dataset;
    }

    /**
     * 查询转赠积分
     * 
     * @author chenghr
     * @param data
     * @param pagination
     * @return dataset
     * @throws Exception
     */
    public static IDataset queryPresentScoreByCity(IData data, Pagination pagination, String eparchyCode) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_QUERYSCOREDONATE_2", data, pagination, eparchyCode);

        return dataset;
    }

    /**
     * 查询转赠积分
     * 
     * @author chenghr
     * @param data
     * @param pagination
     * @return dataset
     * @throws Exception
     */
    public static IDataset queryPresentScoreBySN(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_QUERYSCOREDONATE", data, pagination);

        return dataset;
    }

    /**
     * 查询转赠积分
     * 
     * @author chenghr
     * @param data
     * @param pagination
     * @return dataset
     * @throws Exception
     */
    public static IDataset queryPresentScoreBySN(IData data, Pagination pagination, String eparchyCode) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_QUERYSCOREDONATE", data, pagination, Route.getJourDb(CSBizBean.getTradeEparchyCode()));//订单库路由改为jour用户 duhj

        return dataset;
    }

    public static IDataset queryTradeHis(String USER_ID, String TRADE_TYPE_CODE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("USER_ID", USER_ID);
        cond.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_USERID_TRADETYPECODE", cond, Route.getJourDb());
    }

    public static IDataset queryTradeInfoByOrderId(String orderId, Pagination pg) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT t.trade_id,T.PRODUCT_ID,T.UPDATE_TIME AS UPDATE_TIME_BH,t.ORDER_ID,t.INTF_ID  ");
        parser.addSQL("FROM TF_BH_TRADE T ");
        parser.addSQL("WHERE 1 = 1  ");
        parser.addSQL("AND T.ORDER_ID = :ORDER_ID  ");

        return Dao.qryByParse(parser, pg, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 查询没有正确归档的历史表信息
     * 
     * @author liuxx3
     * @date 2014-08-12
     */
    public static IDataset queryTradeInfoByProIdAndGid(String productId, String groupId, Pagination pg) throws Exception
    {
        String cust_id = "";
        if(groupId!=null&&!groupId.equals("")){
        	IDataset groupInfos = GrpInfoQry.queryGroupCustInfoByGroupId(groupId);
        	if(!groupInfos.isEmpty()){
        		cust_id = groupInfos.getData(0).getString("CUST_ID","");
        	}
        }
    	
    	IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("CUST_ID", cust_id);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT t.trade_id, T.PRODUCT_ID, T.CUST_ID,T.UPDATE_TIME AS UPDATE_TIME_BH,t.ORDER_ID  ");
        parser.addSQL(" FROM TF_BH_TRADE T ");
        parser.addSQL(" WHERE 1 = 1  ");
        parser.addSQL(" AND T.CUST_ID = :CUST_ID  ");
//        parser.addSQL("AND CG.GROUP_ID = :GROUP_ID  ");
        parser.addSQL(" AND T.PRODUCT_ID = :PRODUCT_ID  ");
        parser.addSQL(" and not exists  ");
        parser.addSQL("(Select 1 from TF_B_TRADE_GRP_MERCH m where T.TRADE_ID = M.TRADE_ID AND m.ACCEPT_MONTH  = T.ACCEPT_MONTH) ");

        IDataset merchtradeInfos = Dao.qryByParse(parser, pg, Route.getJourDb(Route.CONN_CRM_CG));
        
        for (int i = 0; i < merchtradeInfos.size(); i++)
        {
            String custId = merchtradeInfos.getData(i).getString("CUST_ID","");
            IData groupInfo = UcaInfoQry.qryCustInfoByCustId(custId);
            merchtradeInfos.getData(i).put("CUST_NAME", groupInfo.getString("CUST_NAME", ""));
            merchtradeInfos.getData(i).put("GROUP_ID", groupInfo.getString("GROUP_ID", ""));
        }
        return merchtradeInfos;
    }

    /**
     * @Title:queryTradeInfoByTradeId
     * @Description: 用于查询台帐是否已经入历史表
     * @param tradeId
     * @return
     * @throws Exception
     * @author chenkh
     * @2014-8-27
     */
    public static IDataset queryTradeInfoByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.TRADE_ID,T.PRODUCT_ID");
        parser.addSQL(" FROM TF_BH_TRADE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.TRADE_ID =:TRADE_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR( :TRADE_ID,5,2))");

        return Dao.qryByParse(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 营改增新增方法2013-12-02 liaolc
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeTypeCodeByTradeId(IData inparam) throws Exception
    {
        SQLParser parser = new SQLParser(inparam);
        parser.addSQL("SELECT TRADE_TYPE_CODE ");
        parser.addSQL("FROM TF_BH_TRADE ");
        parser.addSQL("WHERE TRADE_ID = :TRADE_ID ");
        parser.addSQL("UNION ALL SELECT TRADE_TYPE_CODE  ");
        parser.addSQL("FROM TF_B_TRADE ");
        parser.addSQL("WHERE TRADE_ID = :TRADE_ID ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }

    public static IDataset queryUserPayCount(IData param) throws Exception
    {
        IDataset trades = Dao.qryByCode("TF_BH_TRADE", "SEL_ADVANCE_PAYCOUNT", param);
        return trades;
    }

    public static IDataset queryUserUnFinishTradeCj(IData param) throws Exception
    {
        IDataset trades = Dao.qryByCode("TF_BH_TRADE", "SEL_UNFINISHCJ_BY_USERID", param);
        return trades;
    }
    
    /**
     * 判断是否存在CPE超60G的历史工单
     */
    public static IDataset queryCPETradeInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_USERID_CPETRADEINFO", param);
    }
    
    /**
     * @Title:queryTradeTypeCodeInfoByTradeId
     * @Description: 用于查询台帐是否已经入历史表
     * @param tradeId
     * @return
     * @throws Exception
     * @author chenkh
     * @2014-8-27
     */
    public static IDataset queryTradeTypeCodeInfoByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.TRADE_ID,T.PRODUCT_ID,T.TRADE_TYPE_CODE ");
        parser.addSQL(" FROM TF_BH_TRADE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.TRADE_ID =:TRADE_ID");
        parser.addSQL(" AND ACCEPT_MONTH = TO_NUMBER(SUBSTR( :TRADE_ID,5,2))");

        return Dao.qryByParse(parser, Route.CONN_CRM_CG);
    }
    
    
    /**
     * 查询可取消的无手机开户订单
     * @param //tradeId
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public static IDataset queryCancelNoPhoneTrade(String serialNumber, String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SERIAL_NUMBER", serialNumber);

        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_BH_TRADE T");
        parser.addSQL(" WHERE 1 = 1");
        parser.addSQL(" AND T.TRADE_TYPE_CODE =:TRADE_TYPE_CODE");
        parser.addSQL(" AND T.SERIAL_NUMBER =:SERIAL_NUMBER");
        parser.addSQL(" AND T.CANCEL_TAG = '0'");
        parser.addSQL(" AND T.RSRV_STR9 is null");

        return Dao.qryByParse(parser,Route.getJourDb());
    }
    
    /**
     * 
     */
    public static IDataset queryBhTradeInfoByTradeIdAndTradeCode(String tradeId, String tradeTypeCode, String subscriberState) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SUBSCRIBE_STATE", subscriberState);
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_TRADEID_TRADECODE", param);
    }
    

    /**
     * 账户变更查询
     * duhj
     * @param tradeCityCode
     * @param startDate
     * @param endDate
     * @param startStaffId
     * @param endStaffId
     * @param //routeEparchyCode
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountChgInfo(String tradeCityCode, String startDate, String endDate,String startStaffId, String endStaffId, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_CITY_CODE", tradeCityCode);// 业务类型
        params.put("START_DATE", startDate);// 开始时间
        params.put("END_DATE", endDate);// 结束时间
        params.put("START_STAFF_ID", startStaffId);// 开始工号
        params.put("END_STAFF_ID", endStaffId);// 结束工号
        return Dao.qryByCodeParser("TF_BH_TRADE", "SEL_BY_TRADECODE", params,pagination,Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
	public static int insertBhOrderByOrderId(IData params) throws Exception {
		// TODO Auto-generated method stub
        return  Dao.executeUpdateByCodeCode("TF_B_ORDER", "INS_BHORDER_BY_TRADEID", params, Route.getJourDb(Route.CONN_CRM_CG));

	}

}
