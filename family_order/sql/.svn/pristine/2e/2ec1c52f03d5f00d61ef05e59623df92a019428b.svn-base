UPDATE TF_F_USER_PLATSVC A
   SET A.END_DATE         = (SELECT START_DATE - 1 / 24 / 3600
                               FROM TF_B_TRADE_PLATSVC
                              WHERE ACCEPT_MONTH =
                                    TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                                AND TRADE_ID = :TRADE_ID
                                AND USER_ID = :USER_ID
                                AND SERVICE_ID = :SERVICE_ID
                                AND OPER_CODE = :OPER_CODE
                                AND START_DATE = TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
                                AND USER_ID = A.USER_ID
                                AND SERVICE_ID = A.SERVICE_ID),
       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND USER_ID = :USER_ID
   AND SERVICE_ID = :SERVICE_ID
   AND BIZ_STATE_CODE = 'E'
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
   AND EXISTS
 (SELECT 1
          FROM TF_B_TRADE_PLATSVC
         WHERE ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND TRADE_ID = :TRADE_ID
           AND USER_ID = :USER_ID
           AND SERVICE_ID = :SERVICE_ID
           AND OPER_CODE = :OPER_CODE
           AND START_DATE = TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
           AND USER_ID = A.USER_ID
           AND SERVICE_ID = A.SERVICE_ID)