package com.asiainfo.veris.crm.order.soa.group.upgprelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class UnifyPayRelationQryBean extends GroupBean
{
    
    /**
     * 根据成员号码查询集团统一付费产品的代付关系
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayInfo(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);

        parser.addSQL("SELECT G.GROUP_ID, ");
        parser.addSQL("       G.CUST_NAME, ");
        parser.addSQL("       ACCT.CUST_ID, ");
        parser.addSQL("       ACCT.ACCT_ID, ");
        parser.addSQL("       ACCT.PAY_NAME, ");
        parser.addSQL("       ACCT.BANK_CODE, ");
        parser.addSQL("       ACCT.BANK_ACCT_NO, ");
        parser.addSQL("       U.SERIAL_NUMBER SERIAL_NUMBER, ");
        parser.addSQL("       PAY.PAYITEM_CODE, ");
        parser.addSQL("       PAY.LIMIT / 100 PARA_CODE9, ");
        parser.addSQL("       PAY.START_CYCLE_ID, ");
        parser.addSQL("       PAY.END_CYCLE_ID, ");
        parser.addSQL("       TO_CHAR(PAY.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') PARA_CODE12, ");
        parser.addSQL("       PAY.UPDATE_STAFF_ID PARA_CODE13, ");
        parser.addSQL("       ACCT.PAY_MODE_CODE, ");
        parser.addSQL("       U.CITY_CODE, ");
        parser.addSQL("       U.USER_ID ");
        parser.addSQL("  FROM TF_A_PAYRELATION PAY, TF_F_ACCOUNT ACCT, TF_F_USER U,TF_F_CUST_GROUP G ");
        parser.addSQL(" WHERE ACCT.REMOVE_TAG = '0' ");
        parser.addSQL("   AND ACCT.PARTITION_ID = MOD(PAY.ACCT_ID, 10000) ");
        parser.addSQL("   AND ACCT.ACCT_ID = PAY.ACCT_ID ");
        parser.addSQL("   AND ACCT.RSRV_STR5 = 'UnifyPayProduct' ");
        parser.addSQL("   AND TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD')) BETWEEN PAY.START_CYCLE_ID AND ");
        parser.addSQL("       PAY.END_CYCLE_ID ");
        parser.addSQL("   AND PAY.DEFAULT_TAG = '0' ");
        parser.addSQL("   AND PAY.ACT_TAG = '1' ");
        parser.addSQL("   AND PAY.PARTITION_ID = U.PARTITION_ID ");
        parser.addSQL("   AND PAY.USER_ID = U.USER_ID ");
        parser.addSQL("   AND U.REMOVE_TAG = '0' ");
        parser.addSQL("   AND U.SERIAL_NUMBER = :SERIAL_NUMBER_B ");
        parser.addSQL("   AND G.CUST_ID = ACCT.CUST_ID ");
        parser.addSQL("   AND G.REMOVE_TAG = '0' ");
        
        return Dao.qryByParse(parser, pagination);

    }
    
    /**
     * 通过统一付费产品的user_id 查询统一付费产品的默认账户
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayAcctInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL("SELECT G.GROUP_ID, ");
        parser.addSQL("        G.CUST_NAME, ");
        parser.addSQL("        G.CUST_ID, ");
        parser.addSQL("        ACCT.ACCT_ID, ");
        parser.addSQL("        ACCT.PAY_NAME, ");
        parser.addSQL("        ACCT.BANK_CODE, ");
        parser.addSQL("        ACCT.BANK_ACCT_NO, ");
        parser.addSQL("        ACCT.PAY_MODE_CODE ");
        parser.addSQL("   FROM TF_A_PAYRELATION B, TF_F_ACCOUNT ACCT, TF_F_CUST_GROUP G ");
        parser.addSQL("  WHERE B.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
        parser.addSQL("    AND B.USER_ID = TO_NUMBER(:USER_ID) ");
        parser.addSQL("    AND B.DEFAULT_TAG = '1' ");
        parser.addSQL("    AND B.ACT_TAG = '1' ");
        parser.addSQL("    AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN B.START_CYCLE_ID AND ");
        parser.addSQL("        B.END_CYCLE_ID ");
        parser.addSQL("    AND B.ACCT_ID = ACCT.ACCT_ID ");
        parser.addSQL("    AND ACCT.PARTITION_ID = MOD(B.ACCT_ID, 10000) ");
        parser.addSQL("    AND ACCT.REMOVE_TAG = '0' ");
        parser.addSQL("    AND ACCT.RSRV_STR5 = 'UnifyPayProduct' ");
        parser.addSQL("    AND ACCT.CUST_ID = G.CUST_ID ");
        parser.addSQL("    AND G.REMOVE_TAG = '0' ");
        
        return Dao.qryByParse(parser);
    }
   
    /**
     * 通过统一付费产品的默认账户查询所有付费关系
     * @param param
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryGrpUnifyPayInfoByAcctId(IData param, Pagination pagination) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        
        parser.addSQL(" SELECT U.SERIAL_NUMBER SERIAL_NUMBER, ");
        parser.addSQL("       PAY.PAYITEM_CODE, ");
        parser.addSQL("       PAY.LIMIT / 100 PARA_CODE9, ");
        parser.addSQL("       PAY.START_CYCLE_ID, ");
        parser.addSQL("       PAY.END_CYCLE_ID, ");
        parser.addSQL("       TO_CHAR(PAY.UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') PARA_CODE12, ");
        parser.addSQL("       PAY.UPDATE_STAFF_ID PARA_CODE13, ");
        parser.addSQL("       U.CITY_CODE, ");
        parser.addSQL("       U.USER_ID ");
        parser.addSQL("  FROM TF_A_PAYRELATION PAY, TF_F_USER U ");
        parser.addSQL(" WHERE PAY.ACCT_ID = :ACCT_ID ");
        parser.addSQL("   AND TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD')) BETWEEN PAY.START_CYCLE_ID AND ");
        parser.addSQL("       PAY.END_CYCLE_ID ");
        parser.addSQL("   AND PAY.DEFAULT_TAG = '0' ");
        parser.addSQL("   AND PAY.ACT_TAG = '1' ");
        parser.addSQL("   AND PAY.PARTITION_ID = U.PARTITION_ID ");
        parser.addSQL("   AND PAY.USER_ID = U.USER_ID ");
        parser.addSQL("   AND U.REMOVE_TAG = '0' ");
        
        return Dao.qryByParse(parser, pagination);
    }
    
}