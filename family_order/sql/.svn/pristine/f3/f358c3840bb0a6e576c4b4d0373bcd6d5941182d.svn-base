UPDATE tf_f_user_svcstate
   SET end_date = SYSDATE-1/24/3600
 WHERE user_id = to_number(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date > SYSDATE
   AND (:END_DATE IS NULL OR :END_DATE IS NOT NULL)