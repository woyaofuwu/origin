
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class QueryUserImeiQry extends CSBizBean
{
    public static IDataset queryUserImeiBySN(String serialNumber, String serviceMode, String campnType, Pagination pagination) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("SERVICE_MODE", serviceMode);
        params.put("CAMPN_TYPE", campnType);
        return Dao.qryByCode("TF_F_USER_OTHERSERV", "SEL_BY_SN_IMEI_NEW", params, pagination);
    }
    
    /**
     * 查询特殊营销活动的IMEI
     * 
     */
    public static IDataset getOtherSaleActiveImei(String imeiCode) throws Exception
    {
        IData data = new DataMap();
        data.put("IMEI", imeiCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_B_USER_SALE_ACTIVE_IMEI WHERE 1=1 ");
        parser.addSQL(" AND IMEI = :IMEI ");
        return Dao.qryByParse(parser);
    }
    
    /**
     * 通过华为的dblink查询imei信息
     * 
     */
    public static IDataset getHuaweiImeiInfos(String imeiCode) throws Exception
    {
        IData data = new DataMap();
        data.put("IMEI", imeiCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT B.ATTR_VALUE  AS TERMINAL_TYPE_CODE, ");
        parser.addSQL(" C.PARENT_RES_ID AS DEVICE_MODEL_CODE, ");
        parser.addSQL(" 0 AS DEVICE_COST, ");
        parser.addSQL(" 0 AS SALE_PRICE ");
        parser.addSQL(" FROM IM.IM_INV_MOBTEL@DB_ZDGL A, IM_DICT.IM_RES_TYPE_ATTR_SET@DB_ZDGL B,IM_DICT.IM_RES_TYPE@DB_ZDGL C ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.INV_STATUS = 'INSTORE' ");
        parser.addSQL(" AND A.BUSI_STATUS = 'USABLE' ");
        parser.addSQL(" AND A.INV_ID = :IMEI ");
        parser.addSQL(" AND A.RES_TYPE_ID = B.RES_TYPE_ID ");
        parser.addSQL(" AND A.RES_TYPE_ID = C.RES_TYPE_ID ");
        parser.addSQL(" AND B.ATTR_ID = 'TERMINAL_TYPE_CODE' ");
        return Dao.qryByParse(parser);
    }
    
    
    /**
     * 获取td_b_package_ext的rsrv_str2,rsrv_str5
     * 
     */
    public static IDataset qrySalePackages(String productId, String pacgageId) throws Exception
    {
        IData data = new DataMap();
        data.put("PRODUCT_ID", productId);
        data.put("PACKAGE_ID", pacgageId);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT :PRODUCT_ID AS PRODUCT_ID, :PACKAGE_ID AS PACKAGE_ID, RSRV_STR2, RSRV_STR5 ");
        parser.addSQL(" FROM TD_B_PACKAGE_EXT T ");
        parser.addSQL(" WHERE T.PACKAGE_ID = :PACKAGE_ID ");
        parser.addSQL(" AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE ");
        return Dao.qryByParse(parser, Route.CONN_CRM_CEN);
    }
    
    /**
     * 通过华为的dblink查询imei信息2
     * 
     */
    public static IDataset getHuaweiImeiInfos2(String imeiCode) throws Exception
    {
        IData data = new DataMap();
        data.put("IMEI", imeiCode);
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT C.PARENT_RES_ID AS DEVICE_MODEL_CODE, ");
        parser.addSQL(" C.RES_TYPE_NAME AS DEVICE_MODEL, ");
        parser.addSQL(" C.BRAND_ID AS DEVICE_BRAND_CODE, ");
        parser.addSQL(" D.BRAND_NAME AS DEVICE_BRAND ");
        parser.addSQL(" FROM IM.IM_INV_MOBTEL@DB_ZDGL A, IM_DICT.IM_RES_TYPE_ATTR_SET@DB_ZDGL B, ");
        parser.addSQL(" IM_DICT.IM_RES_TYPE@DB_ZDGL C, IM_DICT.PSI_CFG_RESBRAND@DB_ZDGL D ");
        parser.addSQL(" WHERE 1 = 1 ");
        parser.addSQL(" AND A.INV_STATUS = 'INSTORE' ");
        parser.addSQL(" AND A.BUSI_STATUS = 'USABLE' ");
        parser.addSQL(" AND A.RES_TYPE_ID = B.RES_TYPE_ID ");
        parser.addSQL(" AND A.RES_TYPE_ID = C.RES_TYPE_ID ");
        parser.addSQL(" AND B.ATTR_ID = 'TERMINAL_TYPE_CODE' ");
        parser.addSQL(" AND C.BRAND_ID = D.BRAND_ID ");
        parser.addSQL(" AND A.INV_ID = :IMEI ");
        return Dao.qryByParse(parser);
    }
    
}
