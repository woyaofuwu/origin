select TO_CHAR(INST_ID) INST_ID,
       TO_CHAR(FLOW_ID) FLOW_ID,
       INST_STATE,
       TO_CHAR(USER_ID) USER_ID,
       SERIAL_NUMBER,
       TO_CHAR(ORDER_ID) ORDER_ID,
       TO_CHAR(TRADE_ID) TRADE_ID,
       EPARCHY_CODE,
       TO_CHAR(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       TO_CHAR(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       REMARK
  from TF_B_NAVIGATE_FLOW_INST t
  where T.INST_STATE='1'
  AND INST_ID = :INSTANCE_ID