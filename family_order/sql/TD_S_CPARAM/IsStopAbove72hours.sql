SELECT COUNT(1) recordcount
  FROM tf_f_user_svcstate
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = 0
   AND state_code = '7'
   AND SYSDATE -  start_date > = 3
   AND end_date > SYSDATE