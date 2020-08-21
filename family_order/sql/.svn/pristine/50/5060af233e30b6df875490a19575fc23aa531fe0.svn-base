UPDATE tf_f_user_score_new
   SET score_value=0  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)