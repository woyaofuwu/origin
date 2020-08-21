
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeProductInfoQry
{
    /**
     * 根据tradeId查询所有的用户产品备份数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset getAllTradeBakProductByTradeId(String tradeId) throws Exception
    {
        IData params = new DataMap();
        params.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PRODUCT_BAK", "SEL_ALL_BAK_BY_TRADE", params);
    }

    /**
     * @param trade_id
     * @param discnt_code
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static int getTradeDiscntCount(String trade_id, String discnt_code, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("DISCNT_CODE", discnt_code);
        param.put("MODIFY_TAG", modify_tag);
        IDataset iDataset = Dao.qryByCodeParser("TD_S_CPARAM", "SELCNT_TRADEDSCNT_BY_DSCNTTAG", param);
        int count = iDataset.getData(0).getInt("RECORDCOUNT");
        return count;
    }

    /**
     * 根据业务流水号查询业务台帐产品子表
     * 
     * @param trade_id
     * @param pagination
     * @return
     * @throws Exception
     */
    public static IDataset getTradeProduct(String trade_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT TO_CHAR(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("TO_CHAR(USER_ID) USER_ID, ");
        sql.append("TO_CHAR(USER_ID_A) USER_ID_A, ");
        sql.append("PRODUCT_ID, ");
        sql.append("PRODUCT_MODE, ");
        sql.append("BRAND_CODE, ");
        sql.append("OLD_PRODUCT_ID, ");
        sql.append("OLD_BRAND_CODE, ");
        sql.append("TO_CHAR(INST_ID) INST_ID, ");
        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("INST_ID, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("TO_CHAR(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3, ");
        sql.append("MAIN_TAG ");
        sql.append("FROM TF_B_TRADE_PRODUCT ");
        sql.append("WHERE TRADE_ID = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, param, pagination, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static IDataset getTradeProductBySelByTradeModify(String trade_id, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("MODIFY_TAG", modify_tag);
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_TRADEMODIFY", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * @param inparams
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset getTradeProductByTradeId(IData inparams) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_TRADEID", inparams, Route.CONN_CRM_CG);
    }

    /**
     * 获取产品子台帐
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeProductByTradeId(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_PK", inparams, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    /**
     * 获取产品子台帐
     * 
     * @param trade_id
     * @param user_id
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static IDataset getTradeProductByTradeIdUserId(String trade_id, String user_id, String modify_tag) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);
        inparams.put("USER_ID", user_id);
        inparams.put("MODIFY_TAG", modify_tag);

        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_TRADEID_USERID", inparams);
    }

    /**
     * @param trade_id
     * @param product_id
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static int getTradeProductCount(String trade_id, String product_id, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("PRODUCT_ID", product_id);
        param.put("MODIFY_TAG", modify_tag);
        IDataset iDataset = Dao.qryByCodeParser("TD_S_CPARAM", "SELCNT_TRADEPROD_BY_PRODTAG", param);
        int count = iDataset.getData(0).getInt("RECORDCOUNT");
        return count;
    }

    // 取消预约产品变更获取产品变更信息
    public static IDataset getTradeProductInfosByTradeId(String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        return Dao.qryByCodeParser("TF_B_TRADE_PRODUCT", "SEL_CANCLE_BY_TRADEID", inparams,Route.getJourDb());
    }

    /**
     * @param trade_id
     * @param service_id
     * @param modify_tag
     * @return
     * @throws Exception
     */
    public static int getTradeSvcCount(String trade_id, String service_id, String modify_tag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("SERVICE_ID", service_id);
        param.put("MODIFY_TAG", modify_tag);
        IDataset iDataset = Dao.qryByCodeParser("TD_S_CPARAM", "SELCNT_TRADESVC_BY_SVCTAG", param);
        int count = iDataset.getData(0).getInt("RECORDCOUNT");
        return count;
    }

    /**
     * 根据tradeId查询出备份的产品数据
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryProductFromBakByTradeId(String tradeId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_TRADEPRODUCT_FROM_BAK", param);
    }

    // todo
    /**
     * @Description: 根据集团BBOSS产品订单号和同步序列号 查询出 跨省工单状态产品表(TF_B_PRODUCTTRADE)
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static IDataset queryProductTrade(String SYNC_SEQUENCE, String ORDER_NUMBER) throws Exception
    {
        IData param = new DataMap();
        param.put("SYNC_SEQUENCE", SYNC_SEQUENCE);
        param.put("ORDER_NUMBER", ORDER_NUMBER);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT *  ");
        parser.addSQL(" FROM TF_B_PRODUCTTRADE ");
        parser.addSQL("  WHERE SYNC_SEQUENCE=:SYNC_SEQUENCE  ");

        if (StringUtils.isNotEmpty(param.getString("ORDER_NUMBER")) || StringUtils.isNotBlank(param.getString("ORDER_NUMBER")))
        {
            parser.addSQL(" AND ORDER_NUMBER=:ORDER_NUMBER ");

        }

        return Dao.qryByParse(parser, null, Route.getJourDb(Route.CONN_CRM_CG));

    }

    /**
     * 获取主产品的产品子台账
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryProductTradeMain(String tradeId) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);

        return Dao.qryByCodeParser("TF_B_TRADE_PRODUCT", "SEL_BY_PK_MAIN", inparams);
    }

    /**
     * 查询USER_ID_A
     * 
     * @author liuxx3
     * @date 2014-06-25
     */
    public static IDataset queryProUserIdATrade(String userId, String productId) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_PRO_USER_ID_A", param);

    }

    /**
     * 更新产品开始时间
     * 
     * @author chenzm
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRODUCT", "UPD_STARTDATE", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * 更新产品开始时间
     * 
     * @author yuezy
     * @param trade_id
     * @param start_date
     * @throws Exception
     */
    public static void updateStartDate(String trade_id, String start_date, String end_date, String inst_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("START_DATE", start_date);
        param.put("END_DATE", end_date);
        param.put("INST_ID", inst_id);
        Dao.executeUpdateByCodeCode("TF_B_TRADE_PRODUCT", "UPD_STARTENDDATE", param);
    }

    public static IDataset getTradeProductByUserIdProductIdInstId(String userId, String productId, String instId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
        param.put("INST_ID", instId);
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_USER_ID_PRODUCT_ID_INST_ID", param, Route.getJourDbDefault());
    }
    

    public static IDataset getESOPTradeProductByTradeId(IData params ) throws Exception
    {
        return Dao.qryByCode("TF_B_TRADE_PRODUCT", "SEL_BY_TRADEID", params, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

}
