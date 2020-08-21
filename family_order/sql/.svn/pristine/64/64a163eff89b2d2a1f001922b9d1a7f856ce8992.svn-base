UPDATE tf_f_user_mpute set end_date=sysdate-0.00001
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   and start_date<sysdate
   and end_date>sysdate