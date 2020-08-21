
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class TradeTypeInfoQry
{
    /**
     * 获取登陆地市的业务类型参数
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getTradeTypeForGrp() throws Exception
    {

        SQLParser sql = new SQLParser(null);

        sql.addSQL("select trade_type_code,'[' ||trade_type_code || ']' ||trade_type as trade_type from td_s_tradetype where 1=1");
        sql.addSQL(" and EPARCHY_CODE = '" + CSBizBean.getVisit().getStaffEparchyCode() + "'");
        sql.addSQL(" and remark = 'grp'"); // 只查询集团业务的
        sql.addSQL(" order by trade_type_code");

        IDataset dataset = Dao.qryByParse(sql, Route.CONN_CRM_CEN);
        return dataset;
    }

    public static IDataset qryTradeTypeByEpachyCodeAndprtTradeTeeTag(String eaprchyCode, String tag) throws Exception
    {
        IData param = new DataMap();
        param.put("EPACHY_CODE", eaprchyCode);
        param.put("PRT_TRADEFF_TAG", tag);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select TRADE_TYPE_CODE,TRADE_TYPE");
        parser.addSQL(" from TD_S_TRADETYPE");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and eparchy_code = :EPACHY_CODE ");
        parser.addSQL("  and PRT_TRADEFF_TAG = :PRT_TRADEFF_TAG ");
        parser.addSQL("  and SYSDATE BETWEEN start_date AND end_date ");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /** 查询可返销业务类型 */
    public static IDataset queryCancelTradeType(String tradeTypdeCode, String eparchyCode, String netTypeCode, String rsrvStr1) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("CANCEL_TYPE_CODE", tradeTypdeCode);
        param.put("NET_TYPE_CODE", netTypeCode);
        param.put("RSRV_STR1", rsrvStr1);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select TRADE_TYPE_CODE,TRADE_TYPE");
        parser.addSQL(" from TD_S_TRADETYPE");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and  trade_type_code = :CANCEL_TYPE_CODE ");
        parser.addSQL("  and  net_type_code = :NET_TYPE_CODE ");
        parser.addSQL("  and eparchy_code = :EPARCHY_CODE ");
        parser.addSQL("  and rsrv_str1 = :RSRV_STR1 ");
        parser.addSQL("  and back_tag <>'0' ");
        parser.addSQL("  and start_date < sysdate ");
        parser.addSQL("  and end_date > sysdate");
        parser.addSQL("  order by trade_type_code");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    public static IDataset queryDistincByCode(String tradeTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        return Dao.qryByCode("TD_S_TRADETYPE", "SEL_BY_DISTINC_CODE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryDistincByCodeType(String tradeTypeCode, String tradeType, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("TRADE_TYPE", tradeType);
        return Dao.qryByCode("TD_S_TRADETYPE", "SEL_DISTINC_BY_CODE_TYPE", param, page, Route.CONN_CRM_CEN);
    }

    /** 查询无线固话可返销业务类型 */
    public static IDataset queryTDCancelTradeType(String eparchyCode, String netTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchyCode);
        param.put("NET_TYPE_CODE", netTypeCode);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select TRADE_TYPE_CODE,TRADE_TYPE");
        parser.addSQL(" from TD_S_TRADETYPE");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and eparchy_code = :EPARCHY_CODE ");
        parser.addSQL("  and NET_TYPE_CODE = :NET_TYPE_CODE ");
        parser.addSQL("  and back_tag <>'0' ");
        parser.addSQL("  and TRADE_TYPE_CODE <>'3822' ");
        parser.addSQL("  and start_date < sysdate ");
        parser.addSQL("  and end_date > sysdate");
        parser.addSQL("  order by trade_type_code");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /** 查询可返销业务类型 */
    public static IDataset queryTradeType(String EPARCHY_CODE) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", EPARCHY_CODE);

        SQLParser parser = new SQLParser(param);

        parser.addSQL("select TRADE_TYPE_CODE,TRADE_TYPE");
        parser.addSQL(" from TD_S_TRADETYPE");
        parser.addSQL(" where 1=1 ");
        parser.addSQL("  and eparchy_code = :EPARCHY_CODE ");
        parser.addSQL("  and start_date < sysdate ");
        parser.addSQL("  and end_date > sysdate");
        parser.addSQL("  order by trade_type_code");

        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 查询可以返销的业务（注：湖南本地是写死的哪些业务类型可以返销，并不是按照back_tag配置来的）
     * 
     * @param eaprchyCode
     * @return
     * @throws Exception
     */
    public static IDataset queryUNDOTradeTypeInfos(String eaprchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eaprchyCode);
        return Dao.qryByCode("TD_S_TRADETYPE", "SEL_UNDOTYPE_BY_EPARCHY", param, Route.CONN_CRM_CEN);
    }

    /**
     * @methodName: queryTradeTypeLimitInfos
     * @Description: 查询业务类型未完工工单限制信息
     * @param tradeTypeCode
     * @param eaprchyCode
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2015-2-11 下午4:52:06
     */
    public static IDataset queryTradeTypeLimitInfos(String tradeTypeCode,String eaprchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("LIMIT_ATTR", "0");//正向业务
        param.put("EPARCHY_CODE", eaprchyCode);

        StringBuilder sql = new StringBuilder(2500);

        sql.append(" select TRADE_TYPE_CODE,LIMIT_TRADE_TYPE_CODE,BRAND_CODE,LIMIT_TAG ");
        sql.append(" from TD_S_TRADETYPE_LIMIT ");
        sql.append(" where 1=1 ");
        sql.append(" and TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        sql.append(" and LIMIT_ATTR = :LIMIT_ATTR ");
        sql.append(" and EPARCHY_CODE = :EPARCHY_CODE ");
        sql.append(" and sysdate between start_date and end_date ");

        return Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);
    }
}
