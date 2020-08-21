--IS_CACHE=N
SELECT COUNT(1) 积分数
  FROM CHNL_CU_REGI_PARALLEL CR
 WHERE CR.OPER_CODE = '226'
   AND CR.CAL_FLAG = '2'
   AND CR.CUMU_ACYC >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')
   AND EXISTS
 (SELECT 1
          FROM TF_F_CUSTOMER FC, TF_F_USER FU
         WHERE FC.CUST_ID = FU.CUST_ID
           AND FC.PSPT_TYPE_CODE IN ('0', '1')
           AND FC.PSPT_ID IN (SELECT CU.PSPT_ID
                                FROM TF_F_CUSTOMER CU, TF_F_USER US
                               WHERE CU.CUST_ID = US.CUST_ID
                                 AND US.USER_ID = TO_NUMBER(:USER_ID)
                                 AND US.REMOVE_TAG = '0')
           AND FU.USER_ID = CR.USER_ID)