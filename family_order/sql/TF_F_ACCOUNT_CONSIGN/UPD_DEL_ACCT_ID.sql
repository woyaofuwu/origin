UPDATE tf_f_account_consign
   SET end_cycle_id=(select cycle_id from td_b_cycle where sysdate between cyc_start_time and cyc_end_time),
       update_time=sysdate 
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)