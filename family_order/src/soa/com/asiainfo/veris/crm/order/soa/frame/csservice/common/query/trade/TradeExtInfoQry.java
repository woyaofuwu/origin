
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;

public class TradeExtInfoQry
{

    /**
     * 查询esop台账信息
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeEsopExtByTradeId(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        return Dao.qryByCode("TF_B_TRADE_EXT", "SEL_BY_TRADEID_ESOP", param);
    }

    /**
     * chenyi 查询esop台账信息
     * 
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset getTradeEsopInfoTradeId(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT to_char(trade_id) trade_id, ");
        sql.append("accept_month, ");
        sql.append("ATTR_CODE, ");
        sql.append("ATTR_VALUE, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("rsrv_str1, ");
        sql.append("rsrv_str2, ");
        sql.append("rsrv_str3, ");
        sql.append("rsrv_str4, ");
        sql.append("rsrv_str5, ");
        sql.append("rsrv_str6, ");
        sql.append("rsrv_str7, ");
        sql.append("rsrv_str8, ");
        sql.append("rsrv_str9, ");
        sql.append("rsrv_str10 ");
        sql.append("FROM tf_b_trade_ext ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryBySql(sql, param, Route.getJourDb());
    }

    /*
     * @description 根据tradeId获取esop台帐拓展表数据
     * @author xunyl
     * @date 2013-07-24
     */
    public static IDataset getTradeExtForEsop(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);

        StringBuilder sql = new StringBuilder(1000);

        sql.append("SELECT to_char(trade_id) trade_id, ");
        sql.append("accept_month, ");
        sql.append("ATTR_CODE, ");
        sql.append("ATTR_VALUE, ");
        sql.append("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        sql.append("UPDATE_STAFF_ID, ");
        sql.append("UPDATE_DEPART_ID, ");
        sql.append("REMARK, ");
        sql.append("rsrv_str1, ");
        sql.append("rsrv_str2, ");
        sql.append("rsrv_str3, ");
        sql.append("rsrv_str4, ");
        sql.append("rsrv_str5, ");
        sql.append("rsrv_str6, ");
        sql.append("rsrv_str7, ");
        sql.append("rsrv_str8, ");
        sql.append("rsrv_str9, ");
        sql.append("rsrv_str10 ");
        sql.append("FROM tf_b_trade_ext ");
        sql.append("WHERE trade_id = TO_NUMBER(:TRADE_ID) ");
        sql.append("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
        sql.append("AND attr_code = 'ESOP' ");

        return Dao.qryBySql(sql, param, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 插表
     * 
     * @return
     * @throws Exception
     */
    public static void insertTradeExe(String tradeId, String resultIbsysId, String StaffId, String DepartId) throws Exception
    {

        SQLParser parser = new SQLParser(null);

        parser
                .addSQL("insert into tf_b_trade_ext(TRADE_ID, ACCEPT_MONTH, ATTR_CODE, ATTR_VALUE, UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, RSRV_STR6, RSRV_STR7, RSRV_STR8, RSRV_STR9, RSRV_STR10) ");
        parser.addSQL(" values ('" + tradeId + "', ");
        parser.addSQL("TO_NUMBER(SUBSTR('" + tradeId + "',5,2)), ");
        parser.addSQL("'ESOP', ");
        parser.addSQL("'" + resultIbsysId + "', ");
        parser.addSQL("TO_DATE('" + SysDateMgr.getSysDate() + "', 'YYYY-MM-DD HH24:MI:SS'), ");
        parser.addSQL("'" + StaffId + "', ");
        parser.addSQL("'" + DepartId + "', ");
        parser.addSQL("'', '', '', '', '', '', '', '', '" + resultIbsysId + "', '', '')");
        Dao.executeUpdate(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }

    // todo
    /**
     * @Description 根据登记在TF_B_TRADE_EXT中商品商品订单处理失败的数据
     * @author jch
     * @date
     * @param param
     * @return IDataset
     * @throws Exception
     */

    public static IDataset queryPoError(String condGroupId, String condPospecnumber, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", condGroupId);
        param.put("POSPECNUMBER", condPospecnumber);

        SQLParser parser = new SQLParser(param);
        parser.addSQL("SELECT ext.RSRV_STR1, ext.ATTR_VALUE, p.GROUP_ID, p.MERCH_SPEC_CODE ");
        parser.addSQL(",ext.RSRV_STR10,ext.UPDATE_TIME,ext.UPDATE_STAFF_ID,ext.UPDATE_TIME OPERATE_TIME ");
        parser.addSQL("FROM TF_B_TRADE_EXT ext,tf_b_trade_grp_merchp p ");
        parser.addSQL("WHERE ext.TRADE_ID=p.TRADE_ID ");
        parser.addSQL("and ext.accept_month=TO_NUMBER(SUBSTR(ext.trade_id,5,2)) ");
        parser.addSQL("and p.accept_month=TO_NUMBER(SUBSTR(ext.trade_id,5,2)) ");
        parser.addSQL("and ext.ATTR_CODE like 'BBOSS_%' ");
        parser.addSQL("and p.group_id= :GROUP_ID ");
        parser.addSQL("and p.MERCH_SPEC_CODE= :POSPECNUMBER");

        IDataset resultset = Dao.qryByParse(parser, page, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(resultset))
        {
            for (int i = 0, sizei = resultset.size(); i < sizei; i++)
            {
                IData result = resultset.getData(i);
                result.put("MERCH_SPEC_NAME", PoInfoQry.getPOSpecNameByPoSpecNumber(result.getString("MERCH_SPEC_CODE")));
                result.put("PRODUCT_SPEC_NAME", PoProductQry.getProductSpecNameByProductSpecNumber(result.getString("RSRV_STR1")));
                result.put("UPDATE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(result.getString("UPDATE_STAFF_ID")));
            }
        }
        return resultset;
    }

    /**
     * 根据tradeID查询表的记录
     * 
     * @author liuxx3
     * @date 2014-08-12
     */
    public static IDataset queryPoErrorNew(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
//        Route.getJourDb(BizRoute.getRouteId());
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT to_char(trade_id) trade_id, ");
        parser.addSQL("accept_month, ");
        parser.addSQL("ATTR_CODE, ");
        parser.addSQL("ATTR_VALUE, ");
        parser.addSQL("to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
        parser.addSQL("UPDATE_STAFF_ID, ");
        parser.addSQL("UPDATE_DEPART_ID, ");
        parser.addSQL("REMARK, ");
        parser.addSQL("rsrv_str1, ");
        parser.addSQL("rsrv_str2, ");
        parser.addSQL("rsrv_str3, ");
        parser.addSQL("rsrv_str4, ");
        parser.addSQL("rsrv_str5, ");
        parser.addSQL("rsrv_str6, ");
        parser.addSQL("rsrv_str7, ");
        parser.addSQL("rsrv_str8, ");
        parser.addSQL("rsrv_str9, ");
        parser.addSQL("rsrv_str10 ");
        parser.addSQL("FROM tf_b_trade_ext ");
        parser.addSQL("WHERE 1=1  ");
        parser.addSQL("AND trade_id = TO_NUMBER(:TRADE_ID) ");
        parser.addSQL("and ATTR_CODE like 'BBOSS_%' ");
        parser.addSQL("AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据tradeId 查询 TF_B_POTRADE_STATE数据
     * 
     * @author weixb3
     * @param trade_id
     * @return
     * @throws Exception
     */
    public static IDataset queryPotradeStateByTradeIdForEsop(String trade_id) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        param.put("SYN_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL("select ");
        parser.addSQL(" t.sync_sequence,t.po_order_id,t.ec_serial_number,t.merch_spec_code,t.sync_state,t.syn_time,t.send,t.receive,t.if_provcprt,t.provcprt_type");
        parser.addSQL(" ,t.if_ans,t.trade_id,t.remark,t.EC_NAME ");
        parser.addSQL(" from TF_B_POTRADE_STATE t ");
        parser.addSQL(" where 1=1 ");
        parser.addSQL(" and t.syn_time < :SYN_TIME");
        parser.addSQL(" and t.trade_id =:TRADE_ID");

        return Dao.qryByParse(parser,Route.getJourDb(BizRoute.getRouteId()));
    }

    /**
     * 根据ibisysid 查询tf_b_trade_ext 表
     * 
     * @author weixb3
     * @param ibsys_id
     * @return
     * @throws Exception
     */
    public static IDataset queryTradeExtForEsop(String ibsys_id) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsys_id);
        SQLParser parser = new SQLParser(param);

        parser.addSQL("select t.trade_id ");
        parser.addSQL(" from tf_b_trade_ext t ");
        parser.addSQL(" where 1=1 and t.attr_code = 'ESOP' ");
        parser.addSQL(" and t.rsrv_str8 like '%' || :IBSYSID || '%'");

        return Dao.qryByParse(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新tf_b_trade_ext
     * 
     * @param eosData
     * @param tradeId
     * @throws Exception
     */
    public static void upDateDateTradeEsopExtSTR(IData eosData, String tradeId) throws Exception
    {

        SQLParser parser = new SQLParser(null);
        parser.addSQL("UPDATE tf_b_trade_ext ");
        parser.addSQL("SET RSRV_STR8 = '',RSRV_STR9 = '', ATTR_VALUE = '" + eosData.getString("IBSYSID", "") + "' ");
        parser.addSQL("WHERE TRADE_ID = " + tradeId + " and ACCEPT_MONTH = TO_NUMBER(SUBSTR('" + tradeId + "',5,2)) and ATTR_CODE = 'ESOP'");
        Dao.executeUpdate(parser, Route.getJourDb());
    }

    /**
     * 更新tf_b_trade_ext
     * 
     * @param buf_str8
     * @param esopTradeId
     * @throws Exception
     */
    public static void upDateTradeEsopExt(String buf_str8, String esopTradeId) throws Exception
    {
        SQLParser parser = new SQLParser(null);
        parser.addSQL("UPDATE tf_b_trade_ext ");
        parser.addSQL("SET RSRV_STR8 = '" + buf_str8 + "' ");
        parser.addSQL("WHERE TRADE_ID = " + esopTradeId + " and ACCEPT_MONTH = TO_NUMBER(SUBSTR('" + esopTradeId + "',5,2)) and ATTR_CODE = 'ESOP'");
        Dao.executeUpdate(parser,Route.getJourDb(Route.CONN_CRM_CG));
    }

}
