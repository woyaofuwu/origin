DELETE FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) 
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND start_cycle_id > end_cycle_id
   AND acct_id=TO_NUMBER(:ACCT_ID)