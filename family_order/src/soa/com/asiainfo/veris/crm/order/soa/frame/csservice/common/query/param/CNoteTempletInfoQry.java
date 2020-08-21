
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CNoteTempletInfoQry
{

    /**
     * 获取公共模板项
     * 
     * @param tradeTypeCode
     * @param templetType
     * @return
     * @throws Exception
     */
    public static IDataset getCommonTempletItem(String tradeEparchyCode, String templetType) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
        param.put("TEMPLET_TYPE", templetType);
        SQLParser parser = new SQLParser(param);

        parser.addSQL(" SELECT t.TEMPLET_CODE, ");
        parser.addSQL(" t.TEMPLET_PATH, ");
        parser.addSQL(" t.PRINT_TYPE, ");
        parser.addSQL(" t.PRINT_VERSION, ");
        parser.addSQL(" ti.ITEM_CONTENT ");
        parser.addSQL(" FROM TD_B_CNOTE_TEMPLET t, ");
        parser.addSQL(" TD_B_CNOTE_TEMPLET_ITEM ti, ");
        parser.addSQL(" TD_B_CNOTE_TEMPLET_RELA r ");
        parser.addSQL(" WHERE t.TEMPLET_TYPE = :TEMPLET_TYPE ");
        parser.addSQL(" AND ti.templet_code = r.TEMPLET_CODE ");
        parser.addSQL(" AND r.TEMPLET_CODE = t.TEMPLET_CODE ");
        parser.addSQL(" AND t.USE_TAG = '0' ");
        parser.addSQL(" AND r.RELATION_KIND = '0' ");
        parser.addSQL(" AND r.RELATION_ATTR = :TRADE_EPARCHY_CODE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }

    /**
     * 获取打印模板信息
     * 
     * @param templetCode
     * @return
     * @throws Exception
     */
    public static IDataset getReceiptTempletItems(String templetCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TEMPLET_CODE", templetCode);
        return Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLETITEM", param, Route.CONN_CRM_CEN);
    }

    /**
     * 获取打印模板
     * 
     * @param tradeTypeCode
     * @param templetType
     * @param relationKind
     * @param relationAttr
     * @return
     * @throws Exception
     */
    public static IDataset getReceiptTemplets(String tradeTypeCode, String templetType, String relationKind, String relationAttr) throws Exception
    {
        IData param = new DataMap();
        param.put("TEMPLET_TYPE", templetType);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("RELATION_KIND", relationKind);
        param.put("RELATION_ATTR", relationAttr);
        return Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLET", param, Route.CONN_CRM_CEN);
    }

    public static IDataset getValidTemplet(String RELATION_KIND, String RELATION_ATTR, String TRADE_TYPE_CODE, String TEMPLET_TYPE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("RELATION_KIND", RELATION_KIND);
        param.put("RELATION_ATTR", RELATION_ATTR);
        param.put("TRADE_TYPE_CODE", TRADE_TYPE_CODE);
        param.put("TEMPLET_TYPE", TEMPLET_TYPE);

        return Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLET", param, page, Route.CONN_CRM_CEN);
    }

    public static IDataset getValidTempletItem(String TEMPLET_CODE, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("TEMPLET_CODE", TEMPLET_CODE);
        return Dao.qryByCode("TD_B_CNOTE_TEMPLET", "SEL_VALID_TEMPLETITEM", param, page, Route.CONN_CRM_CEN);
    }
}
