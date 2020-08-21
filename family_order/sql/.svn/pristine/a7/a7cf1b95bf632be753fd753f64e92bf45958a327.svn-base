UPDATE TF_F_USER_OTHER A
   SET A.END_DATE         = (SELECT START_DATE - 1 / 24 / 3600
                               FROM TF_B_TRADE_OTHER
                              WHERE ACCEPT_MONTH =
                                    TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
                                AND TRADE_ID = :TRADE_ID
                                AND USER_ID = :USER_ID
                                AND RSRV_VALUE_CODE = 'V2CP'
                                AND RSRV_VALUE = '32'
                                AND USER_ID = A.USER_ID
                                AND RSRV_VALUE_CODE = A.RSRV_VALUE_CODE
                                AND RSRV_VALUE = A.RSRV_VALUE
                                AND RSRV_STR10 = A.RSRV_STR10),
       A.UPDATE_TIME      = SYSDATE,
       A.UPDATE_STAFF_ID  = :UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID = :UPDATE_DEPART_ID
 WHERE A.PARTITION_ID = MOD(:USER_ID, 10000)
   AND A.USER_ID = :USER_ID
   AND A.RSRV_VALUE_CODE = 'V2CP'
   AND A.RSRV_VALUE = '32'
   AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
   AND EXISTS
 (SELECT 1
          FROM TF_B_TRADE_OTHER
         WHERE ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
           AND TRADE_ID = :TRADE_ID
           AND USER_ID = :USER_ID
           AND RSRV_VALUE_CODE = 'V2CP'
           AND RSRV_VALUE = '32'
           AND USER_ID = A.USER_ID
           AND RSRV_VALUE_CODE = A.RSRV_VALUE_CODE
           AND RSRV_VALUE = A.RSRV_VALUE
           AND RSRV_STR10 = A.RSRV_STR10)