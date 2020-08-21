package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformEomsStateHBean
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformEoms(IDataset param) throws Exception
    {
        return Dao.insert("TF_BH_EOP_EOMS_STATE", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    public static IDataset queryHisEomsStateByIbsysidAndProductNo(String ibsysyid,String productNo) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysyid);
        param.put("PRODUCT_NO", productNo);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL("  FROM TF_BH_EOP_EOMS_STATE T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID ");
        parser.addSQL(" AND T.PRODUCT_NO = :PRODUCT_NO ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));

    }
    
    public static IDataset qryHEomsStateByIbsysidAndRecordNum(String ibsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_BH_EOP_EOMS_STATE", "SEL_BY_IBSYSID_BUSIID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset queryEomsStateByIbsysid(String insysId) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", insysId);

        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.INST_ID, ");
        parser.addSQL("    T.ACCEPT_MONTH, ");
        parser.addSQL("    T.IBSYSID, ");
        parser.addSQL("    T.BUSI_STATE, ");
        parser.addSQL("    T.PRODUCT_NO, ");
        parser.addSQL("    T.RECORD_NUM, ");
        parser.addSQL("    T.SERIALNO, ");
        parser.addSQL("    T.TRADE_ID, ");
        parser.addSQL("    T.DEAL_TYPE, ");
        parser.addSQL("    T.CREATE_DATE, ");
        parser.addSQL("    T.REMARK, ");
        parser.addSQL("    T.RSRV_STR1, ");
        parser.addSQL("    T.RSRV_STR2, ");
        parser.addSQL("    T.RSRV_STR3 ");
        parser.addSQL("  FROM TF_BH_EOP_EOMS_STATE T ");
        parser.addSQL(" WHERE T.IBSYSID = :IBSYSID ");
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));

    }
    
    public static void updHEomsStateByIbsysId(String ibsysid, String busiState) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("BUSI_STATE", busiState);
        Dao.executeUpdateByCodeCode("TF_BH_EOP_EOMS_STATE", "UPD_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryHEomsStateByIbsysidTradeId(String ibsysid, String recordNum) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_BH_EOP_EOMS_STATE", "SEL_DETAIL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void updHEomsStateByPk(String ibsysid, String recordNum, String tradeId, String productNo, String busiState) throws Exception {
        IData param = new DataMap();
        param.put("RECORD_NUM", recordNum);
        param.put("IBSYSID", ibsysid);
        param.put("TRADE_ID", tradeId);
        param.put("PRODUCT_NO", productNo);
        param.put("BUSI_STATE", busiState);
        Dao.executeUpdateByCodeCode("TF_BH_EOP_EOMS_STATE", "UPD_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    }
}
