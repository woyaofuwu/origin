UPDATE tf_f_user_newscore
   SET score=TO_NUMBER(:SCORE)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)