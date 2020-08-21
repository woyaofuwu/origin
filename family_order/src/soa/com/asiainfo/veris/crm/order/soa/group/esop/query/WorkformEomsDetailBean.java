package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformEomsDetailBean {
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformEomsDetail(IData param) throws Exception {
        return Dao.insert("TF_B_EOP_EOMS_DETAIL", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsDetailByIbsysidAndRecordNum(String ibsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_DETAIL", "SEL_BY_IBSYSID_BUSIID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsDetailByIbsysidAndProductNo(String ibsysid, String productNo) throws Exception {
        IData param = new DataMap();
        param.put("PRODUCT_NO", productNo);
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_DETAIL", "SEL_DETAIL_BY_IBSYSID_PRODUCTNO", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsDetailByIbsysidTradeId(String ibsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_DETAIL", "SEL_DETAIL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsDetailByIbsysidAndTradeId(String ibsysid, String tradeId) throws Exception {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_DETAIL", "SEL_DETAIL_BY_IBSYSID_TRADEID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void updEomsDetailByPk(String ibsysid, String recordNum, String tradeId, String productNo, String busiState) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        param.put("TRADE_ID", tradeId);
        param.put("PRODUCT_NO", productNo);
        param.put("BUSI_STATE", busiState);
        Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS_DETAIL", "UPD_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static void updEomsDetailByProductNo(String ibsysid, String recordNum, String productNo, String busiState) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        param.put("PRODUCT_NO", productNo);
        param.put("BUSI_STATE", busiState);
        Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS_DETAIL", "UPD_BY_IBSYSID_PRODUCTNO", param, Route.getJourDb(BizRoute.getRouteId()));

    }

    public static void updEomsDetailByIbsysidRecordNum(String ibsysid, String recordNum, String busiState) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        param.put("BUSI_STATE", busiState);
        Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS_DETAIL", "UPD_BY_IBSYSID_RECORDNUM_STATE", param, Route.getJourDb(BizRoute.getRouteId()));

    }

    public static void delEomsByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS_DETAIL", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsDetailByIbsysid(String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_DETAIL", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEmosDetailInfoByIdNo(IData param) throws Exception {
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_STATE", "SEL_BY_IBSYSIDNO", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset qryEomsStateBySerialNo(IData param) throws Exception {
        return Dao.qryByCodeParser("TF_B_EOP_EOMS_STATE", "SEL_BY_SERIALNO", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static int updateDetailInfo(IData param) throws Exception {
        return Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS_STATE", "UPDATE_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }

    public static IDataset quryEweReleCode(String recordNum, String busiFromId) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("BUSIFORM_ID", busiFromId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.BUSIFORM_NODE_ID,T.ACCEPT_MONTH, ");
        parser.addSQL("       T.SUB_BUSIFORM_ID,T.BUSIFORM_ID, ");
        parser.addSQL("       T.RELE_CODE,T.RELE_VALUE,T.REMARK, ");
        parser.addSQL("       T.VALID_TAG,T.EPARCHY_CODE,T.ACCEPT_DEPART_ID, ");
        parser.addSQL("       T.UPDATE_DEPART_ID,T.UPDATE_STAFF_ID, ");
        parser.addSQL("       T.ACCEPT_STAFF_ID,T.CREATE_DATE, ");
        parser.addSQL("       T.UPDATE_DATE ");
        parser.addSQL("  FROM TF_B_EWE_RELE T ");
        parser.addSQL(" WHERE B.RELE_VALUE = :RECORD_NUM ");
        parser.addSQL("   AND B.BUSIFORM_ID = :BUSIFORM_ID ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));

    }

    public static IDataset queryEomsDetailByIbsysid(String insysId) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", insysId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.IBSYSID, ");
        parser.addSQL("    T.ACCEPT_MONTH, ");
        parser.addSQL("    T.BUSI_ID, ");
        parser.addSQL("    T.BUSI_STATE, ");
        parser.addSQL("    T.BPM_ID, ");
        parser.addSQL("    T.DEAL_STATE, ");
        parser.addSQL("    T.USER_ID, ");
        parser.addSQL("    T.PRODUCT_NO, ");
        parser.addSQL("    T.TRADE_ID, ");
        parser.addSQL("    T.MODIFY_TAG, ");
        parser.addSQL("    T.INSERT_TIME, ");
        parser.addSQL("    T.REMARK, ");
        parser.addSQL("    T.RSRV_STR1, ");
        parser.addSQL("    T.RSRV_STR2, ");
        parser.addSQL("    T.RSRV_STR3 ");
        parser.addSQL("  FROM TF_B_EOP_EOMS_DETAIL T ");
        parser.addSQL(" WHERE T.IBSYSID = :IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));

    }
}
