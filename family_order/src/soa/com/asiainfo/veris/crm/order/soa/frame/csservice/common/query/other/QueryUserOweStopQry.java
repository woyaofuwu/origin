
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 功能：执行欠费停机用户查询 作者：GongGuang
 */
public class QueryUserOweStopQry extends CSBizBean
{
    /**
     * 功能：按客户名称
     */
    public static IDataset queryUserOweStopByCustName(String custName, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("CUST_NAME", custName);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select /*+ index(b IDX_TF_F_CUSTOMER_CUSTNAME)*/");
        parser.addSQL(" A.CITY_CODE,A.SERIAL_NUMBER SERIAL_NUMBER,B.CUST_NAME CUST_NAME,DECODE(A.REMOVE_TAG, '4', '欠费销号', '0', '欠费停机', '未知') REMOVE_TAG,TO_CHAR(A.DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME ");
        parser.addSQL(" FROM TF_F_USER A, TF_F_CUSTOMER B ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND A.CUST_ID = B.CUST_ID AND (A.REMOVE_TAG IN  ('4','0') )  AND (INSTR(A.USER_STATE_CODESET, '9', 1) > 0 OR INSTR(A.USER_STATE_CODESET, '5', 1) > 0) ");
        parser.addSQL("  AND B.CUST_NAME = :CUST_NAME ");
        parser.addSQL("  AND B.PSPT_ID = :PSPT_ID ");
        parser.addSQL("   AND B.PSPT_TYPE_CODE = :PSPT_KIND ");
        return Dao.qryByParse(parser, page);
    }

    /**
     * 功能：按证件ID
     */
    public static IDataset queryUserOweStopByPsptId(String psptId, String psptKind, Pagination page) throws Exception
    {
        IData params = new DataMap();
        params.put("PSPT_ID", psptId);
        params.put("PSPT_KIND", psptKind);
        SQLParser parser = new SQLParser(params);
        parser.addSQL("select /*+ index(b IDX_TF_F_CUSTOMER_PSPTID)*/");
        parser.addSQL(" A.CITY_CODE,A.SERIAL_NUMBER SERIAL_NUMBER,B.CUST_NAME CUST_NAME,DECODE(A.REMOVE_TAG, '4', '欠费销号', '0', '欠费停机', '未知') REMOVE_TAG,TO_CHAR(A.DESTROY_TIME, 'yyyy-mm-dd hh24:mi:ss') DESTROY_TIME ");
        parser.addSQL(" FROM TF_F_USER A, TF_F_CUSTOMER B ");
        parser.addSQL("  WHERE 1=1 ");
        parser.addSQL("  AND A.CUST_ID = B.CUST_ID AND (A.REMOVE_TAG IN  ('4','0') )  AND (INSTR(A.USER_STATE_CODESET, '9', 1) > 0 OR INSTR(A.USER_STATE_CODESET, '5', 1) > 0) ");
        parser.addSQL("  AND B.PSPT_ID = :PSPT_ID ");
        parser.addSQL("   AND B.PSPT_TYPE_CODE = :PSPT_KIND ");
        return Dao.qryByParse(parser, page);
    }

    public QueryUserOweStopQry()
    {
    }
}
