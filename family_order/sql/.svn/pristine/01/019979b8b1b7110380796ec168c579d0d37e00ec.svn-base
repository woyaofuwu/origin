DELETE FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) 
   AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND (select acyc_id from TD_A_ACYCPARA
        where acyc_start_time <= SYSDATE and acyc_end_time > SYSDATE)
        between  start_cycle_id and end_cycle_id