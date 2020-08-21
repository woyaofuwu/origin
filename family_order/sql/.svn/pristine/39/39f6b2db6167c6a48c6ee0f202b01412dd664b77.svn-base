SELECT INST_ID,
       USER_ID,
       SERIAL_NUMBER,
       OLD_STATE_CODE,
       NEW_STATE_CODE,
       TO_CHAR(TRAN_DATE, 'yyyy-mm-dd hh24:mi:ss') TRAN_DATE,
       DEAL_TAG,
       TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,
       RESULT_CODE,
       RESULT_INFO,
       TRADE_ID,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_DEPART_ID,
       UPDATE_STAFF_ID,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3
  FROM TF_F_INTERNETOFTHINGS_BOOK
 WHERE 1 = 1
   AND OLD_STATE_CODE = :OLD_STATE_CODE
   AND NEW_STATE_CODE = :NEW_STATE_CODE
   AND DEAL_TAG = :DEAL_TAG
   AND TRAN_DATE < to_date(:SYS_DATE,'yyyy-mm-dd hh24:mi:ss')
   AND ROWNUM < 51
