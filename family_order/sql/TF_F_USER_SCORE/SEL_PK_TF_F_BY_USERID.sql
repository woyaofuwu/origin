SELECT partition_id,user_id,score_type_code,score_value
  FROM tf_f_user_score
 WHERE user_id=TO_NUMBER(:USER_ID)
AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
 order by score_type_code