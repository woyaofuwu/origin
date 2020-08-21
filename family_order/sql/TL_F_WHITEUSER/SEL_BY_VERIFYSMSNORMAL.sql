SELECT INTF_ID,
       ACCEPT_MONTH,
       TO_CHAR(IN_TIME, 'yyyy-mm-dd hh24:mi:ss') IN_TIME,
       TO_DATE(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_DATE(ENTER_DATE, 'yyyy-mm-dd hh24:mi:ss') ENTER_DATE,
       SERIAL_NUMBER,
       X_CALL_EDMPHONECODE,
       REASON_CODE,
       DATA_ALL,
       NOTICE_CONTENT,
       RESULT_MESSAGE,
       DATA_TYPE,
       PROVINCE_CODE,
       TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,
       PROCESS_TAG,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3,
       REMARK
  FROM TL_B_BLACKSMS A
 WHERE 1 = 1
   AND SERIAL_NUMBER >= :PARA_CODE1
   AND SERIAL_NUMBER <= :PARA_CODE2
   AND PROCESS_TAG in ('A','D')