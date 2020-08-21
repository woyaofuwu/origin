package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import com.ailk.bizservice.dao.CrmDAO;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.dao.DAOManager;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;


public class AbilityPlatOrderBean extends CSBizBean
{
    public static boolean synProductInfo(IData data) throws Exception{
        
        return Dao.insert("TF_B_CTRM_GERLPRODUCT", data, Route.CONN_CRM_CEN);
    }
    
    public static boolean synSubOrderInfo(IData data) throws Exception{
        return Dao.insert("TF_B_CTRM_GERLSUBORDER", data, Route.CONN_CRM_CEN);
    }
    
    public static boolean synOrderInfo(IData data) throws Exception{
        return Dao.insert("TF_B_CTRM_GERLORDER", data, Route.CONN_CRM_CEN);
    }
    
    public static boolean synAdditionallInfo(IData data) throws Exception{
        return Dao.insert("TF_B_CTRM_GERLADTNINFO", data,Route.CONN_CRM_CEN);
    }
    
    public static boolean synGoodsInfo(IData data) throws Exception{
        return Dao.insert("TF_B_CTRM_GERLGOODS", data,Route.CONN_CRM_CEN);
    }
    public static IDataset queryProductorderInfo(String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBORDER_ID", subOrderID);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLPRODUCT", "SEL_PRODUCT_BY_SUBORDER", param, Route.CONN_CRM_CEN);
    }
    public static IDataset queryOrderInfo(String orderId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("ORDER_ID", orderId);
        return Dao.qryByCode("TF_B_CTRM_GERLORDER", "SEL_GERLORDER_INFO_BY_ORDER_ID", inParam, Route.CONN_CRM_CEN);
    }
    public static IDataset querySuborderInfoByNumberOperType(String state, String orderId, String subOrderId) throws Exception {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderId);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLSUBORDER", "SEL_SUBORDER_BY_OPER_STATE", param, Route.CONN_CRM_CEN);
    }

    public static IDataset querySuborderInfo() throws Exception
    {
        IData param = new DataMap();
        return Dao.qryByCodeParser("TF_B_CTRM_GERLSUBORDER", "SEL_SUBORDER_BY_ORDER", param, Route.CONN_CRM_CEN);
    }

    /**
     * 查询TD_B_CTRM_COMMON_PRODUCT表
     * @param contractId
     * @return
     * @throws Exception
     */
    public static IDataset getContractInfo(String contractId) throws Exception
    {
        IData inParam = new DataMap();
        inParam.put("PRODUCT_ID", contractId);
        return Dao.qryByCode("TD_B_CTRM_RELATION", "SEL_CTRM_INFO_BY_PROID", inParam, Route.CONN_CRM_CEN);
    }
    /**
     * 查询合约内容，关联TF_B_CTRM_GERLPRODUCT、TD_B_CTRM_CONTRACT
     * @param subOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGerlProdContractBySubOrderId(String subOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_ORDERID", subOrderId);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLPRODUCT", "SEL_GERLPROD_CONTRACT_BY_SUBORDERID", param, Route.CONN_CRM_CEN);
    }
    /**
     * 查询套餐或增值产品内容，关联TF_B_CTRM_GERLPRODUCT、TD_B_CTRM_VAS
     * @param subOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGerlProdVasBySubOrderId(String subOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_ORDERID", subOrderId);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLPRODUCT", "SEL_GERLPROD_VAS_BY_SUBORDERID", param, Route.CONN_CRM_CEN);
    }
    /**
     * 查询套餐、增值产品或者合约产品，关联TF_B_CTRM_GERLPRODUCT、TD_B_CTRM_COMMON_PRODUCT
     * @param subOrderId
     * @return
     * @throws Exception
     */
    public static IDataset qryGerlProdCommonBySubOrderId(String subOrderId) throws Exception
    {
        IData param = new DataMap();
        param.put("SUB_ORDERID", subOrderId);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLPRODUCT", "SEL_GERLPROD_COMMON_BY_SUBORDERID", param, Route.CONN_CRM_CEN);
    }
    
    public static IDataset querySuborderInfoBy(String state, String orderId, String subOrderId) throws Exception {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderId);
        return Dao.qryByCodeParser("TF_B_CTRM_GERLSUBORDER", "SEL_SUBORDER_BY_OPER_STATE", param, Route.CONN_CRM_CEN);
    }
    /**
     * 查询OAO异步订单
     * @param orderId
     * @param subOrderID
     * @return
     * @throws Exception
     */
    public static IDataset queryOAOorderInfo(String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT * FROM TF_F_OAOORDER_INFO WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询OAO物流上门写卡异步订单
     * @param orderId
     * @param subOrderID
     * @return
     * @throws Exception
     */
    public static IDataset queryOAOwriteCardOrderInfo(String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT * FROM TF_F_OAOWRITECARDORDER_INFO WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    
    public static int  updateOAOorderInfo(String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("UPDATE TF_F_OAOORDER_INFO SET STATE='0' WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
    public static int  updateOAOwriteCardOrderInfo(String state,String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", state);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("UPDATE TF_F_OAOWRITECARDORDER_INFO SET STATE=:STATE WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
    public static int  updateOAOwriteCardOrderInfosimCard(String simCardNo,String emptyCard,String state,String orderId,String subOrderID) throws Exception
    {
        IData param = new DataMap();
        param.put("SIM_CARD_NO", simCardNo);
        param.put("EMPTY_CARD", emptyCard);
        param.put("STATE", state);
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderID);
        StringBuilder sql=new StringBuilder();
        sql.append("UPDATE TF_F_OAOWRITECARDORDER_INFO SET SIM_CARD_NO=:SIM_CARD_NO,EMPTY_CARD=:EMPTY_CARD,STATE=:STATE WHERE ORDER_ID=:ORDER_ID AND SUBORDER_ID=:SUBORDER_ID");
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
    public static IDataset queryOAOStaff(String channelId,String numberOprType) throws Exception
    {
        IData param = new DataMap();
        param.put("CHANNEL_ID", channelId);
        param.put("NUMBER_OPRTYPE", numberOprType);
        StringBuilder sql=new StringBuilder();
        sql.append("SELECT * FROM TL_B_OAOSTAFF WHERE CHANNEL_ID=:CHANNEL_ID AND NUMBER_OPRTYPE=:NUMBER_OPRTYPE");
        return Dao.qryBySql(sql, param,  Route.CONN_CRM_CEN);
    }
    
    public static boolean insertStopMobile(IData data) throws Exception {
        return Dao.insert("TD_B_UNUSUASL_STOPMOBILE", data, Route.CONN_CRM_CEN);
    }

    //查询复审停机信息
    public static IDataset queryStopInfo(IData input) throws Exception {
        IData param = new DataMap();
        param.put("START_TIME", input.getString("START_TIME"));
        param.put("END_TIME", input.getString("END_TIME"));
        return Dao.qryByCodeParser("TD_B_UNUSUASL_STOPMOBILE", "SEL_BY_UPDATE_TIME", param, Route.CONN_CRM_CEN);
    }

    //查询相应停机标记且通知标记为空的记录
    public static IDataset qryAllNotifyFlagIsNull(IData input) throws Exception {
        IData param = new DataMap();
        param.put("STOP_FLAG", input.getString("STOP_FLAG"));
        return Dao.qryByCodeParser("TD_B_UNUSUASL_STOPMOBILE", "SEL_ALL_BY_STOP_FLAG", param, Route.CONN_CRM_CEN);
    }

    public static void updateStopMobileBySn(IData input) throws Exception {
        DBConnection conn = new DBConnection("cen1", true, false);
        try {
            SQLParser parser = new SQLParser(input);
            parser.addSQL(" UPDATE TD_B_UNUSUASL_STOPMOBILE SET ");
            if (StringUtils.isNotBlank(input.getString("STOP_FLAG"))) {
                parser.addSQL(" STOP_FLAG = :STOP_FLAG, ");
                parser.addSQL(" STOP_TIME = SYSDATE, ");
            }
            if (StringUtils.isNotBlank(input.getString("STOP_STAFF_ID"))) {
                parser.addSQL(" STOP_STAFF_ID = :STOP_STAFF_ID, ");
            }
            if (StringUtils.isNotBlank(input.getString("NOTIFY_FLAG"))) {
                parser.addSQL(" NOTIFY_FLAG = :NOTIFY_FLAG, ");
                parser.addSQL(" NOTIFY_TIME = SYSDATE, ");
            }
            parser.addSQL(" UPDATE_TIME = SYSDATE ");
            parser.addSQL(" WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");

            CrmDAO dao = DAOManager.createDAO(CrmDAO.class, Route.CONN_CRM_CEN);
            dao.executeUpdate(conn, parser.getSQL(), parser.getParam());

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
        } finally {
            conn.close();
        }
    }

    //==========hgw TF_F_OAOWRITECARDORDER_INFO_NP 表==========
    public static int  updateOAOwriteCardOrderInfoNP(IData inputs) throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", inputs.getString("STATE", ""));
        param.put("ORDER_ID", inputs.getString("ORDER_ID", ""));
        param.put("SUBORDER_ID", inputs.getString("SUBORDER_ID", ""));
        StringBuilder sql=new StringBuilder();
        sql.append("UPDATE TF_F_OAOWRITECARDORDER_INFO_NP SET STATE=:STATE ");
        if (StringUtils.isNotEmpty(inputs.getString("AUTH_CODE"))) {
            sql.append(" ,AUTH_CODE=:AUTH_CODE ");
            param.put("AUTH_CODE", inputs.getString("AUTH_CODE"));
        }
        if (StringUtils.isNotEmpty(inputs.getString("EXPIRED_TIME"))) {
            sql.append(" ,EXPIRED_TIME=:EXPIRED_TIME ");
            param.put("EXPIRED_TIME", inputs.getString("EXPIRED_TIME"));
        }
        if (StringUtils.isNotEmpty(inputs.getString("AUTH_REQ_ID"))) {
            sql.append(" ,AUTH_REQ_ID=:AUTH_REQ_ID ");
            param.put("AUTH_REQ_ID", inputs.getString("AUTH_REQ_ID"));
        }
        if (StringUtils.isNotEmpty(inputs.getString("EMPTY_CARD"))) {
            sql.append(" ,EMPTY_CARD=:EMPTY_CARD ");
            param.put("EMPTY_CARD", inputs.getString("EMPTY_CARD"));
        }
        if (StringUtils.isNotEmpty(inputs.getString("SIM_CARD_NO"))) {
            sql.append(" ,SIM_CARD_NO=:SIM_CARD_NO ");
            param.put("SIM_CARD_NO", inputs.getString("SIM_CARD_NO"));
        }
        if (StringUtils.isNotEmpty(inputs.getString("EXEC_RESULT"))) {
            sql.append(" ,EXEC_RESULT=:EXEC_RESULT ");
            param.put("EXEC_RESULT", inputs.getString("EXEC_RESULT"));
        }
        sql.append(" where ORDER_ID=:ORDER_ID ");
        sql.append(" AND SUBORDER_ID=:SUBORDER_ID ");
        return Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }

    public static IDataset queryOAOwriteCardOrderInfoNp(String orderId, String subOrderId, String state, String serialNumber, String execState) throws Exception
    {
        IData param = new DataMap();
        param.put("ORDER_ID", orderId);
        param.put("SUBORDER_ID", subOrderId);
        param.put("STATE",state);
        param.put("BILL_ID",serialNumber);
        param.put("EXEC_STATE",execState);

        return Dao.qryByCodeParser("TF_F_OAOWRITECARDORDER_INFO_NP", "SEL_OAOWRITECARDORDER_INFO_NP_BY_SUBORDERID", param, Route.CONN_CRM_CEN);
    }
}
