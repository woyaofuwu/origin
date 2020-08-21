SELECT COUNT(1) recordcount
  FROM tf_f_user_svcstate
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND service_id = 0
   AND state_code = '4'
   AND end_date > SYSDATE