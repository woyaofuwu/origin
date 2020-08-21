package com.asiainfo.veris.crm.order.soa.group.minorec.phonepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;


public class PhoneQRCodePayBean {

    /**
     * 缴费业务类型
     */
    private static final String TRADE_TYPE_CODE = "1604";

    /**
     * 未交费状态
     */
    private static final String SUBSCRIBE_STATE =  "H";

    public static IDataset queryInfos(IData input, Pagination page) throws Exception{
        SQLParser sql = new SQLParser(input);
        sql.addSQL(" SELECT G.GROUP_ID,G.CUST_NAME,U.SERIAL_NUMBER,U.USER_ID,P.PRODUCT_ID,A.ACCT_ID ");
        sql.addSQL(" FROM TF_F_CUST_GROUP   G, ");
        sql.addSQL("      TF_F_USER         U, ");
        sql.addSQL("      TF_F_USER_PRODUCT P, ");
        sql.addSQL("      TF_A_PAYRELATION  A ");
        sql.addSQL(" WHERE G.CUST_ID = U.CUST_ID ");
        sql.addSQL(" AND U.USER_ID = P.USER_ID ");
        sql.addSQL(" AND U.USER_ID = A.USER_ID ");
        sql.addSQL(" AND A.DEFAULT_TAG = '1' ");
        sql.addSQL(" AND TO_DATE(A.END_CYCLE_ID, 'yyyyMMdd') > SYSDATE ");
        sql.addSQL(" AND G.GROUP_ID = :GROUP_ID ");
        sql.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL(" AND P.PRODUCT_ID = :PRODUCT_ID ");
        sql.addSQL(" AND A.ACCT_ID = :ACCT_ID ");
        return Dao.qryByParse(sql,page,Route.CONN_CRM_CG);
    }

    public static void updateTradeState(IData input) throws Exception{
        IData data = new DataMap();

        data.put("SUBSCRIBE_STATE","0");

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_B_TRADE  ");
        sql.append("SET SUBSCRIBE_STATE =:SUBSCRIBE_STATE, ");
        sql.append("INTF_ID = INTF_ID||'TF_B_TRADEFEE_PAYMONEY,' ");
        sql.append("WHERE TRADE_ID = :TRADE_ID ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");

        StringBuilder sql2 = new StringBuilder();
        sql2.append("UPDATE TF_BH_TRADE_STAFF  ");
        sql2.append("SET SUBSCRIBE_STATE ='0' ");
        sql2.append("WHERE TRADE_ID = :TRADE_ID ");
        sql2.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");

        Dao.executeUpdate(sql,data,Route.getJourDb(Route.CONN_CRM_CG));
        Dao.executeUpdate(sql2,data,Route.getJourDb(Route.CONN_CRM_CG));
    }

    public static void updateOrderState(IData data) throws Exception{

        data.put("ORDER_STATE","0");

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_B_ORDER  ");
        sql.append("SET ORDER_STATE =:ORDER_STATE ");
        sql.append("WHERE ORDER_ID = :ORDER_ID ");
        sql.append("AND ACCEPT_MONTH = :ACCEPT_MONTH ");

        Dao.executeUpdate(sql,data,Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IData payTrade(IData inParam) throws Exception{
        String tradeId = inParam.getString("TRADE_ID");
        String orderId = inParam.getString("ORDER_ID");
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        inParam.put("ACCEPT_MONTH",acceptMonth);

        IDataset payFeeDatas = new DatasetList(inParam.getString("PAY_DETAIL"));

        if(IDataUtil.isNotEmpty(payFeeDatas))
        {
            for(int i=0;i<payFeeDatas.size();i++)
            {
                IData temp = payFeeDatas.getData(i);
                temp.put("TRADE_ID", tradeId);
                temp.put("ORDER_ID", orderId);
                temp.put("MONEY", temp.getString("AMOUNT"));
                temp.put("PAY_MONEY_CODE", temp.getString("PAY_TYPE"));
                temp.put("UPDATE_TIME", SysDateMgr.getSysTime());
                temp.put("ACCEPT_MONTH", acceptMonth);
                temp.put("UPDATE_STAFF_ID", inParam.getString("UPDATE_STAFF_ID"));
                temp.put("UPDATE_DEPART_ID", inParam.getString("UPDATE_DEPART_ID"));
                temp.put("REMARK", temp.getString("REMARK", "pay sucess!"));
                //Dao.executeUpdate(new StringBuilder(insPayMoney), temp, Route.getJourDb());
                //Dao.executeUpdateByCodeCode("TF_B_TRADEFEE_PAYMONEY", "INSERT_TRADEFEE_PAYMONEY2", temp, Route.getJourDb());
                Dao.insert("TF_B_TRADEFEE_PAYMONEY",temp,Route.getJourDb(Route.CONN_CRM_CG));
            }

            updateTradeState(inParam);
            updateOrderState(inParam);
        }

        IData outData = new DataMap();
        outData.put("RESULT", "SUCCESS");

        return outData;
    }

    public static IDataset queryOrderInfos(IData param) throws Exception{
        param.put("TRADE_TYPE_CODE",TRADE_TYPE_CODE);
        param.put("SUBSCRIBE_STATE",SUBSCRIBE_STATE);

        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT * FROM TF_B_TRADE T ");
        sql.addSQL(" WHERE T.TRADE_TYPE_CODE = :TRADE_TYPE_CODE ");
        sql.addSQL(" AND T.CUST_ID = :CUST_ID ");
        sql.addSQL(" AND T.PRODUCT_ID = :PRODUCT_ID ");
        sql.addSQL(" AND T.ACCT_ID = :ACCT_ID ");
        sql.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL(" AND T.SUBSCRIBE_STATE = :SUBSCRIBE_STATE ");

        return Dao.qryByParse(sql,Route.getJourDb(Route.CONN_CRM_CG));

    }

    public static IData queryTradeFee(String tradeId) throws Exception{
        IData param = new DataMap();
        param.put("TRADE_ID",tradeId);

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT T.*,DECODE(T.FEE_MODE, '0', '营业费用', '1', '押金', '2', '预存', '其他') FEE_TYPE ");
        sql.append(" FROM TF_B_TRADEFEE_SUB T ");
        sql.append(" WHERE T.TRADE_ID =:TRADE_ID ");

        IDataset results = Dao.qryBySql(sql,param,Route.getJourDb(Route.CONN_CRM_CG));
        if(DataUtils.isEmpty(results)){
            return new DataMap();
        }
        return results.first();
    }

    public static IData cancelTrade(IData data) throws Exception{
        StringBuilder sql1 = new StringBuilder();
        sql1.append(" DELETE FROM TF_B_TRADE T WHERE T.TRADE_ID=:TRADE_ID ");

        StringBuilder sql2 = new StringBuilder();
        sql2.append(" DELETE FROM TF_BH_TRADE_STAFF T WHERE T.TRADE_ID=:TRADE_ID ");

        StringBuilder sql3 = new StringBuilder();
        sql3.append(" DELETE FROM TF_B_TRADEFEE_SUB T WHERE T.TRADE_ID=:TRADE_ID ");

        StringBuilder sql4 = new StringBuilder();
        sql4.append(" DELETE FROM TF_B_ORDER T WHERE T.ORDER_ID=:ORDER_ID ");

        if(StringUtils.isNotBlank(data.getString("TRADE_ID"))){
            Dao.executeUpdate(sql1,data,Route.getJourDb(Route.CONN_CRM_CG));
            Dao.executeUpdate(sql2,data,Route.getJourDb(Route.CONN_CRM_CG));
            Dao.executeUpdate(sql3,data,Route.getJourDb(Route.CONN_CRM_CG));
        }

        if(StringUtils.isNotBlank(data.getString("ORDER_ID"))){
            Dao.executeUpdate(sql4,data,Route.getJourDb(Route.CONN_CRM_CG));
        }

        IData result = new DataMap();
        result.put("RESULT","SUCCESS");
        return result;
    }

    public static IDataset queryCustInfoByName(IData param) throws Exception{
        String custName = param.getString("CUST_NAME");
        if(StringUtils.isBlank(custName)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到集团客户名称！");
        }
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM TF_F_CUST_GROUP T WHERE T.CUST_NAME LIKE '%");
        sql.append(custName);
        sql.append("%' ");

        return Dao.qryBySql(sql,param,Route.CONN_CRM_CG);
    }
}
