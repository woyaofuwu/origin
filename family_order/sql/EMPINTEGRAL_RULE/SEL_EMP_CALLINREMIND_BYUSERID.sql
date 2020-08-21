SELECT DEPART_KIND_CODE 部门类型, ACCEPT_DATE 受理月份, TRADE_ID 台账标识
  FROM (SELECT E.DEPART_KIND_CODE,
               TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,
               A.TRADE_ID
          FROM UCR_CRM1.TF_BH_TRADE    A,
               UCR_CRM1.TF_B_TRADE_SVC B,
               TD_M_DEPART             E
         WHERE A.ACCEPT_MONTH = B.ACCEPT_MONTH
           AND A.TRADE_ID = B.TRADE_ID
           AND A.USER_ID = B.USER_ID
           AND A.TRADE_TYPE_CODE IN ('110', '10')
           AND A.USER_ID = TO_NUMBER(:USER_ID)
           AND A.CANCEL_TAG = '0'
           AND B.MODIFY_TAG = '0'
           AND SYSDATE BETWEEN B.START_DATE AND B.END_DATE
           AND B.SERVICE_ID = 231
           AND TRUNC(A.ACCEPT_DATE) BETWEEN TRUNC(SYSDATE - 3) AND TRUNC(SYSDATE)
           AND A.TRADE_DEPART_ID = E.DEPART_ID
           AND ROWNUM < 2
        UNION
        SELECT E.DEPART_KIND_CODE,
               TO_CHAR(A.ACCEPT_DATE, 'YYYYMM') ACCEPT_DATE,
               A.TRADE_ID
          FROM UCR_CRM1.TF_BH_TRADE A, TD_M_DEPART E
         WHERE A.TRADE_DEPART_ID = E.DEPART_ID
           AND A.USER_ID = TO_NUMBER(:USER_ID)
           AND A.TRADE_TYPE_CODE IN (60)
           AND EXISTS
         (SELECT 1
                  FROM TF_F_USER U, TF_F_USER_PRODUCT UP, TF_F_USER_SVC US
                 WHERE U.PARTITION_ID = UP.PARTITION_ID
                   AND U.PARTITION_ID = US.PARTITION_ID
                   AND U.USER_ID = UP.USER_ID
                   AND U.USER_ID = US.USER_ID
                   AND U.USER_ID = A.USER_ID
                   AND U.REMOVE_TAG = '0'
                   AND U.ACCT_TAG = '0'
                   AND U.OPEN_DATE >= TRUNC(SYSDATE) - 3
                   AND UP.MAIN_TAG = '1'
                   AND UP.PRODUCT_ID IN
                       (SELECT TO_NUMBER(CP.PARAM_CODE)
                          FROM TD_S_COMMPARA CP
                         WHERE CP.SUBSYS_CODE = 'CSM'
                           AND CP.PARAM_ATTR = 830
                           AND SYSDATE BETWEEN CP.START_DATE AND CP.END_DATE)
                   AND US.SERVICE_ID = 231
                   AND SYSDATE BETWEEN UP.START_DATE AND UP.END_DATE
                   AND SYSDATE BETWEEN US.START_DATE AND US.END_DATE)) TRADE
 WHERE ROWNUM < 2