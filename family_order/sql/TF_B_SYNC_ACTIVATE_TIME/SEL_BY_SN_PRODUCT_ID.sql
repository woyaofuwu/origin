SELECT ORDER_ID,
       TRADE_ID,
       SERIAL_NUMBER,
       TO_CHAR(ACTIVATE_TIME, 'YYYY-MM-DD HH24:MI:SS') ACTIVATE_TIME,
       PRODUCT_ID,
       PRODUCT_NAME,
       TO_CHAR(START_DATE, 'YYYY-MM-DD HH24:MI:SS') START_DATE,
       STATE,
       TO_CHAR(UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,
       RESULT_CODE,
       RESULT_INFO,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       TO_CHAR(RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,
       RSRV_TAG1,
       RSRV_TAG2
  FROM TF_B_SYNC_ACTIVATE_TIME
 WHERE SERIAL_NUMBER = :SERIAL_NUMBER
   AND PRODUCT_ID = :PRODUCT_ID
 ORDER BY UPDATE_TIME DESC
