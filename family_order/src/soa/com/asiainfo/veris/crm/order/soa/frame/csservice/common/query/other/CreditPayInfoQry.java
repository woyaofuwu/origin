
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CreditPayInfoQry
{

    /**
     * 查询是否返销记录
     * 
     * @param tradeId
     * @param backTag
     * @return
     * @throws Exception
     */
    public static IDataset getAllLogByBackTag(String tradeId, String backTag) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("BACK_TAG", backTag);

        return Dao.qryByCode("TL_B_CREDITPAYLOG", "SEL_ALL_LOG_X", param);
    }

    public static IDataset getAllLogs(String tradeId, String buyBack) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("BUY_BACK", buyBack);

        return Dao.qryByCode("TL_B_CREDITPAYLOG", "SEL_ALL_LOG", param);
    }

    public static boolean insPayLog(IData param) throws Exception
    {
        return Dao.insert("TL_B_CREDITPAYLOG", param);
    }

    /**
     * 信用卡分期购机支付记录查询
     * 
     * @param serialNum
     * @param page
     * @return
     * @throws Exception
     */
    public static IDataset queryPaymentRecord(String serialNum, Pagination page) throws Exception
    {
        IData param = new DataMap();
        IDataset result = new DatasetList();
        IDataset temp = new DatasetList();
        IDataset result2 = new DatasetList();
        param.put("SERIAL_NUMBER", serialNum);
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());

        SQLParser parser = new SQLParser(param);

        /*
         * T.TRADE_ID,--工单号 T.CUST_NAME,--客户名称 T.SERIAL_NUMBER,--服务号码 T.ACCEPT_DATE,--交易日期 A.OPER_FEE,--营业费
         * A.FOREGIFT,--担保费 A.ADVANCE_PAY,--预存款 D.DEVICE_MODEL,--终端型号名 D.DEVICE_BRAND,--终端品牌 D.RES_CODE --终端串码
         */
        // 终端预存营销
        parser.addSQL("SELECT /*+ index(T IDX_TF_B_TRADE_SN) */T.TRADE_ID,T.CUST_NAME,T.SERIAL_NUMBER,T.ACCEPT_DATE, A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY AMOUNT,D.DEVICE_MODEL,D.DEVICE_BRAND,D.RES_CODE,'终端预存营销' TRADE_TYPE,T.TRADE_TYPE_CODE,C.MONEY ");
        parser.addSQL(" FROM TF_B_TRADE T LEFT JOIN TL_B_CREDITPAYLOG C ON C.TRADE_ID = T.TRADE_ID AND C.BUY_BACK = '0', TF_B_TRADE_SALE_ACTIVE A, TF_B_TRADE_SALE_GOODS D ");
        parser.addSQL(" WHERE A.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND A.TRADE_ID = D.TRADE_ID ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE = '240' ");
        parser.addSQL(" AND A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY <> 0");
        parser.addSQL(" AND T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND NOT EXISTS( SELECT 1 FROM TL_B_CREDITPAYLOG C WHERE C.TRADE_ID = T.TRADE_ID AND (C.BUY_BACK ='1' OR C.BUY_BACK ='2')) ");
        parser.addSQL(" UNION ALL ");
        parser
                .addSQL("SELECT /*+ index(T IDX_TF_BH_TRADE_SN) */T.TRADE_ID,T.CUST_NAME,T.SERIAL_NUMBER,T.ACCEPT_DATE, A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY AMOUNT,D.DEVICE_MODEL,D.DEVICE_BRAND,D.RES_CODE,'终端预存营销' TRADE_TYPE,T.TRADE_TYPE_CODE,C.MONEY ");
        parser.addSQL(" FROM TF_BH_TRADE T LEFT JOIN TL_B_CREDITPAYLOG C ON C.TRADE_ID = T.TRADE_ID AND C.BUY_BACK = '0', TF_B_TRADE_SALE_ACTIVE A, TF_B_TRADE_SALE_GOODS D ");
        parser.addSQL(" WHERE A.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND A.TRADE_ID = D.TRADE_ID ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE = '240' ");
        parser.addSQL(" AND A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY <> 0");
        parser.addSQL(" AND T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND NOT EXISTS( SELECT 1 FROM TL_B_CREDITPAYLOG C WHERE C.TRADE_ID = T.TRADE_ID AND (C.BUY_BACK ='1' OR C.BUY_BACK ='2')) ");
        parser.addSQL(" UNION ALL ");
        // 裸机销售
        parser
                .addSQL("SELECT /*+ index(T IDX_TF_B_TRADE_SN) */T.TRADE_ID , T.CUST_NAME , T.SERIAL_NUMBER , T.ACCEPT_DATE , to_number(T.RSRV_STR7) AMOUNT, T.RSRV_STR2 DEVICE_MODEL, T.RSRV_STR4 DEVICE_BRAND, T.RSRV_STR1 RES_CODE, '裸机销售' TRADE_TYPE , T.TRADE_TYPE_CODE , C.MONEY ");
        parser.addSQL(" FROM TF_B_TRADE T ");
        parser.addSQL(" LEFT JOIN TL_B_CREDITPAYLOG C ");
        parser.addSQL(" ON C.TRADE_ID = T.TRADE_ID ,TD_S_COMMPARA S ");
        parser.addSQL(" WHERE T.TRADE_TYPE_CODE = '253' ");
        parser.addSQL(" AND T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND T.RSRV_STR3 in (select S.PARAM_CODE from TD_S_COMMPARA S ");
        parser.addSQL(" where S.SUBSYS_CODE='CSM' ");
        parser.addSQL(" AND S.PARAM_ATTR='1218' ");
        parser.addSQL(" AND (S.EPARCHY_CODE='ZZZZ' or S.EPARCHY_CODE=T.TRADE_EPARCHY_CODE ) ");
        parser.addSQL(" AND S.END_DATE>=T.ACCEPT_DATE )");
        parser.addSQL(" AND NOT EXISTS ");
        parser.addSQL(" (SELECT 1 ");
        parser.addSQL(" FROM TL_B_CREDITPAYLOG C ");
        parser.addSQL(" WHERE C.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND (C.BUY_BACK = '1' OR C.BUY_BACK = '2')) ");
        parser.addSQL(" UNION ALL ");
        parser
                .addSQL("SELECT /*+ index(T IDX_TF_BH_TRADE_SN) */T.TRADE_ID , T.CUST_NAME , T.SERIAL_NUMBER , T.ACCEPT_DATE , to_number(T.RSRV_STR7) AMOUNT, T.RSRV_STR2 DEVICE_MODEL, T.RSRV_STR4 DEVICE_BRAND, T.RSRV_STR1 RES_CODE, '裸机销售' TRADE_TYPE , T.TRADE_TYPE_CODE , C.MONEY ");
        parser.addSQL(" FROM TF_BH_TRADE T ");
        parser.addSQL(" LEFT JOIN TL_B_CREDITPAYLOG C ");
        parser.addSQL(" ON C.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" WHERE T.TRADE_TYPE_CODE = '253' ");
        parser.addSQL(" AND T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND T.RSRV_STR3 in (select S.PARAM_CODE from TD_S_COMMPARA S ");
        parser.addSQL(" where S.SUBSYS_CODE='CSM' ");
        parser.addSQL(" AND S.PARAM_ATTR='1218' ");
        parser.addSQL(" AND (S.EPARCHY_CODE='ZZZZ' or S.EPARCHY_CODE=T.TRADE_EPARCHY_CODE ) ");
        parser.addSQL(" AND S.END_DATE>=T.ACCEPT_DATE ) ");
        parser.addSQL(" AND NOT EXISTS ");
        parser.addSQL(" (SELECT 1 ");
        parser.addSQL(" FROM TL_B_CREDITPAYLOG C ");
        parser.addSQL(" WHERE C.TRADE_ID = T.TRADE_ID ");
        parser.addSQL(" AND (C.BUY_BACK = '1' OR C.BUY_BACK = '2')) ");
        // 四码合一
        parser.addSQL(" UNION ALL ");
        parser.addSQL("SELECT /*+ index(T IDX_TF_B_TRADE_SN) */T.TRADE_ID,T.CUST_NAME,T.SERIAL_NUMBER,T.ACCEPT_DATE, 0 AMOUNT,'手机' DEVICE_MODEL,'手机' DEVICE_BRAND,A.RSRV_STR1 RES_CODE,'四码合一' TRADE_TYPE,T.TRADE_TYPE_CODE,C.MONEY ");
        parser.addSQL(" FROM TF_B_TRADE T LEFT JOIN TL_B_CREDITPAYLOG C ON C.TRADE_ID = T.TRADE_ID AND C.BUY_BACK = '0' , TF_B_TRADE_OTHER A ");
        parser.addSQL(" WHERE T.TRADE_ID = A.TRADE_ID AND T.TRADE_TYPE_CODE = '642' ");
        parser.addSQL(" AND　T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND NOT EXISTS( SELECT 1 FROM TL_B_CREDITPAYLOG C WHERE C.TRADE_ID = T.TRADE_ID AND (C.BUY_BACK ='1' OR C.BUY_BACK ='2')) ");
        parser.addSQL(" UNION ALL ");
        parser.addSQL("SELECT /*+ index(T IDX_TF_BH_TRADE_SN) */T.TRADE_ID,T.CUST_NAME,T.SERIAL_NUMBER,T.ACCEPT_DATE, 0 AMOUNT,'手机' DEVICE_MODEL,'手机' DEVICE_BRAND,A.RSRV_STR1 RES_CODE,'四码合一' TRADE_TYPE,T.TRADE_TYPE_CODE,C.MONEY ");
        parser.addSQL(" FROM TF_BH_TRADE T LEFT JOIN TL_B_CREDITPAYLOG C ON C.TRADE_ID = T.TRADE_ID AND C.BUY_BACK = '0' , TF_B_TRADE_OTHER A ");
        parser.addSQL(" WHERE T.TRADE_ID = A.TRADE_ID AND T.TRADE_TYPE_CODE = '642' ");
        parser.addSQL(" AND T.ACCEPT_DATE > TRUNC(SYSDATE) ");
        parser.addSQL(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        parser.addSQL(" AND T.TRADE_STAFF_ID = :UPDATE_STAFF_ID ");
        parser.addSQL(" AND T.CANCEL_TAG = '0' ");
        parser.addSQL(" AND NOT EXISTS( SELECT 1 FROM TL_B_CREDITPAYLOG C WHERE C.TRADE_ID = T.TRADE_ID AND (C.BUY_BACK ='1' OR C.BUY_BACK ='2')) ");
        result = Dao.qryByParse(parser, page);

        return result;
    }

    public static int updPayLog(IData param) throws Exception
    {
        return Dao.executeUpdateByCodeCode("TL_B_CREDITPAYLOG", "UPDATE_BUY_BACK", param);
    }
}
