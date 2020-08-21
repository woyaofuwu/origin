
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeGrpMerchpDiscntInfoQry
{

    /**
     * 根据tradeId和modifytag删除TF_B_TRADE_GRP_MERCHP_DISCNT表数据
     * 
     * @param tradeId
     * @param modifyTag
     * @throws Exception
     */
    public static void deleteMerchpDiscntByTradeIdModifyTag(String tradeId, String modifyTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("MODIFY_TAG", modifyTag);

        StringBuilder sql = new StringBuilder();

        sql.append("delete from TF_B_TRADE_GRP_MERCHP_DISCNT b ");
        sql.append("where b.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("and b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("and b.modify_tag = :MODIFY_TAG ");

        Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * chenyi 2014-03-04 根据discnt_code获取产品资费信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getMerchpDisInfoByDiscntCode(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT INST_ID, ");
        sql.append("to_char(TRADE_ID) TRADE_ID, ");
        sql.append("ACCEPT_MONTH, ");
        sql.append("to_char(USER_ID) USER_ID, ");
        sql.append("MERCH_SPEC_CODE, ");
        sql.append("PRODUCT_ORDER_ID, ");
        sql.append("PRODUCT_OFFER_ID, ");
        sql.append("PRODUCT_SPEC_CODE, ");
        sql.append("PRODUCT_DISCNT_CODE, ");
        sql.append("to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
        sql.append("to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
        sql.append("MODIFY_TAG, ");
        sql.append("nvl(OPER_CODE, MODIFY_TAG) OPER_CODE, ");
        sql.append("IS_NEED_PF, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("RSRV_NUM1, ");
        sql.append("RSRV_NUM2, ");
        sql.append("RSRV_NUM3, ");
        sql.append("to_char(RSRV_NUM4) RSRV_NUM4, ");
        sql.append("to_char(RSRV_NUM5) RSRV_NUM5, ");
        sql.append("RSRV_STR1, ");
        sql.append("RSRV_STR2, ");
        sql.append("RSRV_STR3, ");
        sql.append("RSRV_STR4, ");
        sql.append("RSRV_STR5, ");
        sql.append("to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
        sql.append("to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
        sql.append("to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, ");
        sql.append("RSRV_TAG1, ");
        sql.append("RSRV_TAG2, ");
        sql.append("RSRV_TAG3 ");
        sql.append("from TF_B_TRADE_GRP_MERCHP_DISCNT a ");
        sql.append("WHERE a.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND a.product_discnt_code = :PRODUCT_DISCNT_CODE ");

        return Dao.qryBySql(sql, param, Route.getJourDb());
    }

    /**
     * chenyi 2014-03-04 将merchpDiscnt信息插入表中
     * 
     * @throws Exception
     */
    public static void insertMerchpDiscntInfo(IData param) throws Exception
    {

        Dao.insert("TF_B_TRADE_GRP_MERCHP_DISCNT", param, Route.getJourDb());
    }

    /**
     * @description 更新BBOSS侧资费信息的回收标记(RSRV_STR2字段)
     * @author xunyl
     * @date 2014-08-29
     */
    public static void updateDelFlag(String tradeId, String discntCode, String delFlag, String isNeedPf) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", tradeId);
        inparams.put("PRODUCT_DISCNT_CODE", discntCode);
        inparams.put("RSRV_STR2", delFlag);
        inparams.put("IS_NEED_PF", isNeedPf);
        StringBuilder sql = new StringBuilder(1000);

        sql.append("update TF_B_TRADE_GRP_MERCHP_DISCNT b ");
        sql.append("set rsrv_str2  = :RSRV_STR2, ");
        sql.append("is_need_pf= :IS_NEED_PF ");
        sql.append("where b.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("and b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("and b.product_discnt_code = :PRODUCT_DISCNT_CODE ");

        Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据TRADE_ID更新TF_B_TRADE_GRP_MERCHP_DISCNT表IS_NEED_PF字段
     * 
     * @author chenyi 更新modify_tag 更新不发pf
     * @param trade_id
     * @throws Exception
     */
    public static void updateMechpDiscntIsSendPfByTradeid(String trade_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("UPDATE ");
        sql.append("TF_B_TRADE_GRP_MERCHP_DISCNT T ");
        sql.append("SET  T.IS_NEED_PF =DECODE(T.IS_NEED_PF,'','0','1','0',T.IS_NEED_PF) ");
        sql.append("WHERE T.TRADE_ID= :TRADE_ID AND ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        Dao.executeUpdate(sql, inparams, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 根据USER_ID和PRODUCT_DISCNT_CODE更新TF_B_TRADE_GRP_MERCHP_DISCNT表MODIFY_TAG、END_DATE、START_DATE、RSRV_STR2、IS_NEED_PF字段
     * 
     * @author ft
     * @param param
     * @throws Exception
     */
    public static void updateMerchpDistinctState(IData param) throws Exception
    {
        StringBuilder sql = new StringBuilder(1000);

        sql.append("update TF_B_TRADE_GRP_MERCHP_DISCNT b ");
        sql.append("set MODIFY_TAG = :MODIFY_TAG, ");
        sql.append("end_date   = to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'), ");
        sql.append("start_date = to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'), ");
        sql.append("rsrv_str1  = :RSRV_STR1, ");
        sql.append("rsrv_str2  = :RSRV_STR2, ");
        sql.append("IS_NEED_PF = :IS_NEED_PF ");
        sql.append("where b.trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("and b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("and b.user_id = :USER_ID ");
        sql.append("and b.product_discnt_code = :PRODUCT_DISCNT_CODE ");

        Dao.executeUpdate(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }
}
