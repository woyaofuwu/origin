UPDATE tf_f_user_score
   SET score_value=TO_NUMBER(:SCORE_VALUE)  
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)