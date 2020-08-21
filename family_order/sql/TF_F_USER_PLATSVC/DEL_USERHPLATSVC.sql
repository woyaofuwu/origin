DELETE TF_F_USER_PLATSVC A
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.USER_ID = :USER_ID
   AND EXISTS
 (SELECT 1
          FROM TF_B_TRADE_PLATSVC
         WHERE ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND TRADE_ID = :TRADE_ID
           AND USER_ID = TO_NUMBER(A.USER_ID)
           AND SERVICE_ID = A.SERVICE_ID
           AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
           AND NOT (OPER_CODE IN ('06', '11') OR SP_CODE LIKE 'SW%' OR
                OPER_CODE IN ('89', '99') ))