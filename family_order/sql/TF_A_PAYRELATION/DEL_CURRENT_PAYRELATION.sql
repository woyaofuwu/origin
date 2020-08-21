DELETE FROM tf_a_payrelation
 WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=mod(TO_NUMBER(:USER_ID),10000)
   AND default_tag=:DEFAULT_TAG
   AND act_tag=:ACT_TAG
   AND start_cycle_id < end_cycle_id
   AND end_cycle_id > :END_CYCLE_ID