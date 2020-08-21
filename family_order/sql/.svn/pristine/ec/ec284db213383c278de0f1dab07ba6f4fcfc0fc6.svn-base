UPDATE tf_a_payrelation
   SET end_cycle_id=:END_CYCLE_ID,update_time=SYSDATE  
 WHERE user_id=TO_NUMBER(:USER_ID) 
 	AND partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND default_tag=:DEFAULT_TAG
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND act_tag=:ACT_TAG