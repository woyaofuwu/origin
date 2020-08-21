select to_char(TRADE_ID) TRADE_ID,
       ACCEPT_MONTH,
       to_char(USER_ID) USER_ID,
       to_char(INST_ID) INST_ID,
       to_char(RELA_INST_ID) RELA_INST_ID,
       SERVICE_CODE,
       USAGE_STATE,
       BILLING_TYPE,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       MODIFY_TAG,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5
  FROM TF_B_TRADE_PCRF 
 WHERE trade_id = to_number(:TRADE_ID) 
   and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))