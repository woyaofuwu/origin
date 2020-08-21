UPDATE tf_f_account_consign
   SET end_cycle_id=to_number(to_char(add_months(to_date(:CYCLE_ID,'yyyymm'),-1),'yyyymm')),
       update_time=sysdate 
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND START_CYCLE_ID <= END_CYCLE_ID
   AND END_CYCLE_ID > to_number(to_char(add_months(to_date(:CYCLE_ID,'yyyymm'),-1),'yyyymm'))