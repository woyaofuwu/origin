SELECT SERIAL_NUMBER,to_char(USER_ID) USER_ID,to_char(START_TIME,'yyyy-mm-dd hh24:mi:ss') START_TIME,to_char(END_TIME,'yyyy-mm-dd hh24:mi:ss') END_TIME,to_char(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,OTHER_PARTY,SMS_CONTENT,RECORD_COUNT,IDTYPE,DEAL_TAG,to_char(IN_TIME,'yyyy-mm-dd hh24:mi:ss') IN_TIME,ACCEPT_MONTH,to_char(DEAL_TIME,'yyyy-mm-dd hh24:mi:ss') DEAL_TIME,to_char(FINISH_TIME,'yyyy-mm-dd hh24:mi:ss') FINISH_TIME,RESULT_CODE,RESULT_INFO,to_char(SEND_TIME,'yyyy-mm-dd hh24:mi:ss') SEND_TIME,SEND_STATUS,RSRV_STR1,RSRV_STR2,RSRV_STR3,MSGID
  FROM TI_BI_AO_MON
 WHERE (serial_number = :SERIAL_NUMBER or :SERIAL_NUMBER IS NULL)
   AND EXEC_TIME >= TO_DATE(:START_TIME,'yyyy-mm-dd hh24:mi:ss')
   AND EXEC_TIME <= TO_DATE(:END_TIME,'yyyy-mm-dd hh24:mi:ss')