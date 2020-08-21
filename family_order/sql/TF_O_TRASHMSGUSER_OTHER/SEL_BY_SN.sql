SELECT SERIAL_NUMBER,SMS_TYPE,CUST_TYPE,STATE_CODE,to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,RSRV_STR1,RSRV_STR2,RSRV_STR3
  FROM TF_O_TRASHMSGUSER_OTHER
 WHERE (serial_number = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
   AND cust_type = :CUST_TYPE
   AND state_code = :STATE_CODE
   AND sms_type = :SMS_TYPE
   AND SYSDATE BETWEEN start_date and end_date