SELECT /*+ parallel(A,3)(B,3) full(A) */ TO_CHAR(A.ACCEPT_DATE,'yyyy-mm-dd HH24:MI:SS') ACCEPT_DATE,
       A.TRADE_STAFF_ID,
       A.SERIAL_NUMBER,
       A.REMARK,
       A.RSRV_STR8,
       B.DEVICE_NO_S,
       B.DEVICE_NO_E,
       B.RSRV_STR1,       
       B.SALE_PRICE / 100 SALE_PRICE,
       B.TRADE_ID,
       B.DEVICE_NUM,
       B.DEVICE_TYPE_CODE,
       A.TRADE_TYPE_CODE 
          FROM TF_BH_TRADE A, TF_B_TRADEFEE_DEVICE B
 WHERE 1=1
           AND A.TRADE_CITY_CODE = :CITY_CODE
           AND A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE 
           AND A.TRADE_STAFF_ID >= TRIM(:STAFF_ID_S)
           AND A.TRADE_STAFF_ID <= TRIM(:STAFF_ID_E)
           AND A.ACCEPT_DATE >= TO_DATE(:START_VALUECARD_SALE_TIME, 'yyyy-mm-dd HH24:MI:SS')
           AND A.ACCEPT_DATE <= TO_DATE(:END_VALUECARD_SALE_TIME, 'yyyy-mm-dd HH24:MI:SS')
       AND A.TRADE_ID = B.TRADE_ID
       AND  A.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)
       AND  B.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)
       AND (B.DEVICE_NO_S BETWEEN :X_RES_NO_S AND :X_RES_NO_E OR B.DEVICE_NO_E BETWEEN :X_RES_NO_S AND :X_RES_NO_E OR :X_RES_NO_S BETWEEN B.DEVICE_NO_S AND B.DEVICE_NO_E OR  :X_RES_NO_E BETWEEN B.DEVICE_NO_S AND B.DEVICE_NO_E)
