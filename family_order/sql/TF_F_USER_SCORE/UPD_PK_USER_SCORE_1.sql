UPDATE tf_f_user_score
   SET score_value=:SCORE_VALUE
 WHERE user_id=:USER_ID
AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND score_type_code=:SCORE_TYPE_CODE