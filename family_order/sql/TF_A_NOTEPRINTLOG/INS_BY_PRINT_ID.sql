insert into tf_a_noteprintlog 
select   
  EPARCHY_CODE  ,
 f_sys_getseqid('','SEQ_NOTE_PRINT_ID') PRINT_ID,
  TO_NUMBER(TO_CHAR(SYSDATE,'MM')) PARTITION_ID,
  BATCH_ID ,CHARGE_ID ,NOTE_CODE , NOTE_NO,ACCT_ID, PAY_NAME ,
  SERIAL_NUMBER ,USER_ID,MIN_BILL_ID,MAX_BILL_ID ,SCORE_VALUE,CREDIT_VALUE        ,
  ROUND_FEE,LAST_BALANCE,REAL_FEE,PRESENT_FEE         ,
  TOTAL_FEE           ,
  LATE_FEE            ,
  START_DATE          ,
  END_DATE            ,
  CHEQUE_NO           ,
  PRINT_MODE          ,
  sysdate PRINT_TIME  ,
  :PRINT_STAFF_ID     PRINT_STAFF_ID,
  :PRINT_DEPART_ID    PRINT_DEPART_ID,
  :PRINT_CITY_CODE    PRINT_CITY_CODE,
  :PRINT_EPARCHY_CODE PRINT_EPARCHY_CODE,
  '1' REPRINT_FLAG    ,
  :PRINT_REASON      PRINT_REASON  ,
  CANCEL_TAG      ,
  CANCEL_STAFF_ID     ,
  CANCEL_DEPART_ID    ,
  CANCEL_CITY_CODE    ,
  CANCEL_EPARCHY_CODE ,
  CANCEL_REASON       ,
  CANCEL_TIME,
  POST_TAG            ,
  RSRV_FEE1           ,
  RSRV_FEE2           ,
  RSRV_INFO1          ,
  RSRV_INFO2     ,
  link_charge_id  
  from tf_a_noteprintlog  where print_id =TO_NUMBER(:PRINT_ID) AND partition_id=TO_NUMBER(:PARTITION_ID)