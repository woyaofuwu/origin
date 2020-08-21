SELECT INTF_ID,
       MONTH,
       TO_CHAR(IN_TIME, 'yyyy-mm-dd hh24:mi:ss') IN_TIME,
       START_DATE,
       ENTER_DATE,
       SUBSTR(SERIAL_NUMBER, 3) SERIAL_NUMBER,
       X_CALL_EDMPHONECODE,
       DATA_TYPE,
       REASON_CODE,
       PROCESS_TAG,
       TRADE_NUMBER,
       MSG_ID,
       NOTICE_CONTENT,
       TO_CHAR(EXEC_TIME, 'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       REMARK
  FROM TL_B_MONITORFILE
 WHERE INTF_ID = :INTF_ID
 AND PROCESS_TAG IN ('0', '6', 'B') 
