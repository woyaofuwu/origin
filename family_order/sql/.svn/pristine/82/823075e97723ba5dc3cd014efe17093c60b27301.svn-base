UPDATE tf_a_invoicetype
   SET end_acyc_id=:END_ACYC_ID,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS'),update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  
WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND start_acyc_id<=:CURR_ACYC_ID
   AND end_acyc_id>=:CURR_ACYC_ID