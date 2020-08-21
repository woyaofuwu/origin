UPDATE tf_f_user_res
   SET end_date=SYSDATE  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND end_date >= SYSDATE